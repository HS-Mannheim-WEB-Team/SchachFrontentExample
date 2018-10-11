package frontend;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import schach.backend.BackendSpielAdminStub;
import schach.backend.BackendSpielStub;
import schach.daten.D;
import schach.daten.Xml;

public class Frontend extends JFrame{
	public static final int updateInterval=1;

	private static final long serialVersionUID = 1L;

	private JMenuBar menu=new JMenuBar();
	private JMenu mSpiel=new JMenu("Spiel");
	public final JMenuItem mSpielNeu=new JMenuItem("Neu");
	public final JMenuItem mSpielLaden=new JMenuItem("Laden");
	public final JMenuItem mSpielSpeichern=new JMenuItem("Speichern");
	private JMenu mVerwaltung=new JMenu("Verwaltung");
	public final JMenuItem mVerwaltungEinstellungen=new JMenuItem("Einstellungen");
	public final JMenuItem mVerwaltungInfo=new JMenuItem("Info");

	private JPanel panelBelegung=new JPanel();
	private JLabel labelBelegung=new JLabel();
	private Belegung belegung=null;

	private JPanel panelHistorie=new JPanel();
	private JScrollPane jScrollerHistorie;
	private JTextArea jLog=new JTextArea();
	private JScrollPane jScrollerLog;
	
	private JList<String> zugListe=new JList<String>();
	private boolean inHistorienAnsicht=false;
	private JButton weiterspielenButton=new JButton("Weiterspielen...");
	
	private EventHandlerBelegung events=null;
	private BackendSpielStub backendSpiel=null;
	private BackendSpielAdminStub backendSpielAdmin=null;

	private boolean binWeiss=true;
	private int anzahlZuege=-1;
	private boolean ende=false;

	public static String toZeichen(int wert){
		return ""+(char)(96+wert);
	}
	
	public static String toSchachNotation(int x,int y){
		return toZeichen(x)+y;
	}
	
	public static int[] toArrayNotation(String schachNotation){ //x,y
		int[] ergebnis=new int[2];
		ergebnis[0]=((int)schachNotation.toCharArray()[0])-96;
		ergebnis[1]=Integer.parseInt(""+schachNotation.toCharArray()[1]);
		return ergebnis;
	}
	
	public static BufferedImage kopiereBild(BufferedImage quelle){
    BufferedImage kopie=new BufferedImage(quelle.getWidth(),quelle.getHeight(),quelle.getType());
    Graphics g=kopie.getGraphics();
    g.drawImage(quelle,0,0,null);
    g.dispose();
    return kopie;
	}

	private Frontend(String url){
		backendSpiel=new BackendSpielStub(url);
		backendSpielAdmin=new BackendSpielAdminStub(url);
		events=new EventHandlerBelegung(this);

		// MENU
		JPanel panelMenu=new JPanel(); 
		panelMenu.setLayout(new BorderLayout());
		initialisiereMenu();
		panelMenu.add(menu,BorderLayout.NORTH);
		add(panelMenu,BorderLayout.NORTH);
		
		// SPIELBELEGUNG
		panelBelegung.setLayout(null);
		panelBelegung.addMouseListener(events);
		labelBelegung.setLayout(null);
		labelBelegung.setOpaque(false);
		labelBelegung.setSize(445,540);
		panelBelegung.add(labelBelegung);

		// HISTORIE
		panelHistorie.setLayout(new BorderLayout());
		JPanel p=new JPanel();
		p.add(panelHistorie);
		jScrollerHistorie=new JScrollPane(p,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		jScrollerHistorie.setPreferredSize(new Dimension(300,400));
		weiterspielenButton.setBackground(new Color(200,200,200));
		weiterspielenButton.setForeground(Color.BLACK);
		weiterspielenButton.setHorizontalAlignment(SwingConstants.LEFT);
		weiterspielenButton.setEnabled(false);
		weiterspielenButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isInHistorienAnsicht()) setHistorienAnsicht(false);
			}
		});

		// SPIELBELEGUNG + HISTORIE EINTRAGEN
		JSplitPane splitter=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,panelBelegung,jScrollerHistorie);
		splitter.setDividerLocation(450);
		add(splitter,BorderLayout.CENTER);

		// LOGGER
		jLog.setLineWrap(true);
		jScrollerLog=new JScrollPane(jLog,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		jScrollerLog.setPreferredSize(new Dimension(150,150));
		add(jScrollerLog,BorderLayout.SOUTH);
		
		setSize(700,750);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public Frontend(boolean binWeiss) {
		this(Parameter.zumServer);
		String s="Franks Schach-Engine ";
		this.binWeiss=binWeiss;
		if (binWeiss)
			setTitle(s+" - Spieler WEISS");
		else
			setTitle(s+" - Spieler SCHWARZ");
		new Updater(this,updateInterval);
	}
	
	public Frontend(boolean binWeiss,KI ki) {
		this(Parameter.zumServer);
		if (ki==null)
			throw new RuntimeException("Es muss eine gueltige KI uebergeben werden!");
		ki.init(binWeiss,backendSpiel);
		ki.setFrontend(this);
		ki.start();
		String s="Franks Schach-Engine "+ki.getClass().getSimpleName();
		this.binWeiss=binWeiss;
		if (binWeiss)
			setTitle(s+" - Spieler WEISS");
		else
			setTitle(s+" - Spieler SCHWARZ");
		new Updater(this,updateInterval);
	}
	
	public void setBelegung(Belegung belegung){
		this.belegung=belegung;
		updateBelegung(belegung.getBild());
	}
	
	private void updateBelegung(Image bildNeu){
		labelBelegung.setIcon(new ImageIcon(bildNeu));		
	}
	
	public Belegung getBelegung(){
		return belegung;
	}
	
	public BackendSpielStub getBackendSpiel(){
		return backendSpiel;
	}
	
	public BackendSpielAdminStub getBackendSpielAdmin(){
		return backendSpielAdmin;
	}

	public EventHandlerBelegung getEventHandler(){
		return events;
	}
	
	public boolean ichSpieleWeiss(){
		return binWeiss;
	}
	public boolean ichSpieleSchwarz(){
		return !ichSpieleWeiss();
	}
	
	public boolean ichBinAmZug(){
		return(ichSpieleWeiss()==(getAnzahlZuege()%2==0));
	}
	
	public int getAnzahlZuege() {
		return anzahlZuege;
	}

	public void setAnzahlZuege(int anzahlZuege) {
		this.anzahlZuege = anzahlZuege;
	}
	
	public void resetHistorie(){
		panelHistorie.removeAll();
	}

	public void markiereFelder(int x,int y,ArrayList<String> felderErlaubt){
		markiereFelder(toSchachNotation(x,y),felderErlaubt);
	}
	public void markiereFelder(String feldMarkiert,ArrayList<String> felderErlaubt){
		int xFeld=toArrayNotation(feldMarkiert)[0];
		int yFeld=toArrayNotation(feldMarkiert)[1];
		if ((xFeld==0)||(yFeld==0)) return;
		int[] viereck=new int[4];
		BufferedImage im=kopiereBild(belegung.getBild());
		Graphics2D g=(Graphics2D) im.getGraphics();
		g.setStroke(new BasicStroke(3));
		if ((felderErlaubt!=null)&&(felderErlaubt.size()>0)){
			for(String feld:felderErlaubt){
				if (feld==null) continue;
				int xFeldErlaubt=toArrayNotation(feld)[0];
				int yFeldErlaubt=toArrayNotation(feld)[1];
				viereck=getFeldStart(xFeldErlaubt,yFeldErlaubt);
				g.setColor(new Color(255,255,0));
				g.drawRect(viereck[0],viereck[1],50,50);
			}			
		}
		g.setColor(new Color(255,0,0));
		viereck=getFeldStart(xFeld,yFeld);
		g.drawRect(viereck[0],viereck[1],50,50);
		g.dispose();
		updateBelegung(im);
	}
	
	private int[] getFeldStart(int x,int y){
		int[] erg=new int[4];
		if (ichSpieleWeiss()){
			erg[0]=(x-1)*50+20; // x1
			erg[1]=420-(y-1)*50; // y1			
		}
		else{
			erg[0]=470-((x+1)*50); // x1
			erg[1]=y*50+20; // y1			
		}
		return erg;
	}
	
	private void initialisiereMenu(){
		mSpiel.add(mSpielNeu); mSpielNeu.addActionListener(events);
		mSpiel.add(mSpielLaden); mSpielLaden.addActionListener(events);
		mSpiel.add(mSpielSpeichern); mSpielSpeichern.addActionListener(events);
		menu.add(mSpiel);
		mVerwaltung.add(mVerwaltungEinstellungen); mVerwaltungEinstellungen.addActionListener(events);
		mVerwaltung.add(mVerwaltungInfo); mVerwaltungInfo.addActionListener(events);
		menu.add(mVerwaltung);
	}

	public void updateLog() {
		ArrayList<D> zugHistorie=Xml.toArray(backendSpiel.getZugHistorie(Parameter.idSpiel));
		resetHistorie();
		zugListe.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		zugListe.setBackground(null);
		DefaultListModel<String> model=new DefaultListModel<String>();
		zugListe.setModel(model);
		for(D datenwert:zugHistorie){
			String zug=datenwert.getString("zug");
			if ((zug!=null)&&(zug.length()>0)) model.addElement(zug);
		}
		zugListe.addMouseListener(new MouseAdapter() {
	     public void mouseClicked(MouseEvent e) {
	    	 @SuppressWarnings("unchecked")
	    	 int index=((JList<String>)e.getSource()).locationToIndex(e.getPoint());
	    	 setHistorienAnsicht(true);
	    	 Belegung b=new Belegung(backendSpiel.getBelegung(Parameter.idSpiel,index+1),binWeiss);
	    	 setBelegung(b);
	     }
	  });
		zugListe.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		zugListe.setVisibleRowCount(-1);
		
		panelHistorie.add(zugListe,BorderLayout.CENTER);
		panelHistorie.add(weiterspielenButton,BorderLayout.SOUTH);
		jScrollerHistorie.validate();
		jScrollerHistorie.getVerticalScrollBar().setValue(jScrollerHistorie.getVerticalScrollBar().getMaximum());
		jScrollerHistorie.repaint();
	}
	
	public void resetLog(){
		jLog.setText("");
	}
	
	public void printLog(String text){
		jLog.setText(jLog.getText()+text);
	}

	public void printlnLog(String text){
		printLog(text+"\n");
	}
	
	public void log(String text){
		Date d=new Date();
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		printLog(df.format(d)+": "+text+"\n");
	}

	public void setEnde(boolean ende) {
		this.ende=ende;
	}
	public boolean istZuEnde(){
		return ende;
	}

	public boolean isInHistorienAnsicht() {
		return inHistorienAnsicht;
	}

	public void setHistorienAnsicht(boolean inHistorienAnsicht) {
		if (inHistorienAnsicht){
			weiterspielenButton.setEnabled(true);
		}
		else{
			zugListe.clearSelection();
			weiterspielenButton.setEnabled(false);			
			Belegung b=new Belegung(backendSpiel.getAktuelleBelegung(Parameter.idSpiel),binWeiss);
   	 	setBelegung(b);
		}
		this.inHistorienAnsicht = inHistorienAnsicht;
	}
}
