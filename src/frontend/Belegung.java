package frontend;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import javax.imageio.ImageIO;

import schach.daten.D;
import schach.daten.D_Belegung;
import schach.daten.D_Figur;
import schach.daten.FigurEnum;
import schach.daten.SpielEnum;
import schach.daten.Xml;

public class Belegung {
	private static BufferedImage[] figurenWeiss=new BufferedImage[6];
	private static BufferedImage[] figurenSchwarz=new BufferedImage[6];
	
	static{
		for(int i=0;i<figurenWeiss.length;i++){
			try {
				figurenWeiss[i]=ImageIO.read(new File(Parameter.pfadFiguren+i+"-w"+Parameter.endungFiguren));
				figurenSchwarz[i]=ImageIO.read(new File(Parameter.pfadFiguren+i+"-s"+Parameter.endungFiguren));	
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}	
		}
	}
	
	private boolean binWeiss=true;
	private D_Belegung daten;
	private ArrayList<D_Figur> datenFigurenAufBrett=new ArrayList<D_Figur>();
	private ArrayList<D_Figur> datenFigurenGeschlagen=new ArrayList<D_Figur>();
	private Feld[][] feld=new Feld[9][9];
	
	public Belegung(String xml,boolean binWeiss) {
		this.binWeiss=binWeiss;
		ArrayList<D> spielDaten=Xml.toArray(xml.toString());
		int counter=0;
		daten=(D_Belegung)spielDaten.get(counter);
    counter++;
    // Felder auf dem Brett initialisieren
    boolean isWeissFeld=false;
		for(int i=1;i<=8;i++){
			for(int j=1;j<=8;j++){
				feld[i][9-j]=new Feld(isWeissFeld);
				isWeissFeld=!isWeissFeld;
			}
			isWeissFeld=!isWeissFeld;
		}
    // Figuren dieser Belegung auf dem Brett
    for(int j=1;j<=daten.getInt("anzahlFigurenAufBrett");j++){
    	D_Figur datenFigur=(D_Figur)spielDaten.get(counter);
	    counter++;
	    datenFigurenAufBrett.add(datenFigur);
	    String position=datenFigur.getString("position");
	    int[] xy=toArrayNotation(position);
	    feld[xy[0]][xy[1]].setDatenFigur(datenFigur);
    }
    // geschlagene Figuren dieser Belegung
    for(int j=1;j<=daten.getInt("anzahlFigurenGeschlagen");j++){
    	D_Figur datenFigur=(D_Figur)spielDaten.get(counter);
	    counter++;
	    datenFigurenGeschlagen.add(datenFigur);
    }
	}
	
	public D_Belegung getDaten(){
		return daten;
	}
	
	public boolean isWeissImSchach(){
		return SpielEnum.WeissImSchach.toString().equals(daten.getString("status"));
	}
	
	public boolean isSchwarzImSchach(){
		return SpielEnum.SchwarzImSchach.toString().equals(daten.getString("status"));
	}

	public boolean isWeissSchachMatt(){
		return SpielEnum.WeissSchachMatt.toString().equals(daten.getString("status"));
	}

	public boolean isSchwarzSchachMatt(){
		return SpielEnum.SchwarzSchachMatt.toString().equals(daten.getString("status"));
	}
	
	public boolean isPatt(){
		return SpielEnum.Patt.toString().equals(daten.getString("status"));
	}

	public BufferedImage getBild(){
		int groesse=Parameter.groesseFeld;
		BufferedImage im=new BufferedImage(groesse*8+45,groesse*8+140,BufferedImage.TYPE_INT_RGB);
		Graphics2D g=(Graphics2D)im.getGraphics();
		g.setColor(Parameter.farbeBrettHintergrund);
		g.fillRect(0,0,im.getWidth(null),im.getHeight(null));
		// Brett
		g.drawImage(getBildBrett(binWeiss),20,70,null);
		// Beschriftung des Bretts
		zeichneBeschriftung(im,g,binWeiss);
		// weisse geschlagene Figuren
		g.drawImage(getBildGeschlagen(binWeiss),20,10,null);
		// schwarze geschlagene Figuren
		g.drawImage(getBildGeschlagen(!binWeiss),20,groesse*8+100,null);
		g.dispose();
		return im;
	}
	
	private void zeichneBeschriftung(BufferedImage im,Graphics2D g,boolean weiss){
		int groesse=Parameter.groesseFeld;
		for (int i=1;i<=8;i++){
			g.setFont(new Font("Arial",Font.BOLD,18));
			g.setColor(new Color(0,0,0));
			if (weiss){
				g.drawString(""+i,3,im.getHeight(null)-(88+groesse*(i-1)));
				g.drawString(""+i,26+groesse*8,im.getHeight(null)-(88+groesse*(i-1)));				
				g.drawString(toZeichen(i),15+groesse/2+groesse*(i-1),63);
				g.drawString(toZeichen(i),15+groesse/2+groesse*(i-1),90+groesse*8);
			}
			else{
				g.drawString(""+(9-i),3,im.getHeight(null)-(88+groesse*(i-1)));
				g.drawString(""+(9-i),26+groesse*8,im.getHeight(null)-(88+groesse*(i-1)));
				g.drawString(toZeichen(9-i),15+groesse/2+groesse*(i-1),63);
				g.drawString(toZeichen(9-i),15+groesse/2+groesse*(i-1),90+groesse*8);
			}
		}
	}
	
	private static String toZeichen(int wert){
		return ""+(char)(96+wert);
	}
	
	private static int[] toArrayNotation(String schachNotation){
		try{
			char[] daten=schachNotation.toCharArray();
			int x=Integer.parseInt(""+(daten[0]-96));
			int y=Integer.parseInt(""+daten[1]);
			return new int[]{x,y};
		}
		catch (Exception e){
			return null;
		}
	}
	
	private Image getBildGeschlagen(boolean isWeiss){
		int groesse=Parameter.groesseFeld;
		Image im=new BufferedImage(groesse*8,groesse/2+4,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=(Graphics2D)im.getGraphics();
		g.setColor(Parameter.farbeBrettHintergrund);
		g.fillRect(0,0,im.getWidth(null),im.getHeight(null));
		int anzahl=0;
		Comparator<D_Figur> figurComparator=new FigurComparatorTyp();
		datenFigurenGeschlagen.sort(figurComparator);
		for(D_Figur datenFigur:datenFigurenGeschlagen){
			if (isWeiss==datenFigur.getBool("isWeiss")){
				g.drawImage(getBildFigur(datenFigur,true),2+(groesse/2*anzahl),2,null);
				anzahl++;
			}
		}
		g.dispose();			
		return im;
	}

	private Image getBildBrett(boolean vorneWeiss){
		int groesse=Parameter.groesseFeld;
		Image im=new BufferedImage(groesse*8,groesse*8,BufferedImage.TYPE_INT_RGB);
		Graphics2D g=(Graphics2D)im.getGraphics();
		if (vorneWeiss){
			for(int i=1;i<=8;i++){
				for(int j=1;j<=8;j++){
					g.drawImage(feld[i][j].getBildFeld(),groesse*(i-1),groesse*(8-j),null);
				}
			}			
		}
		else{
			for(int i=1;i<=8;i++){
				for(int j=1;j<=8;j++){
					g.drawImage(feld[i][j].getBildFeld(),groesse*(8-i),groesse*(j-1),null);
				}
			}						
		}
		g.dispose();
		return im;
	}
	
	private Image getBildFigur(D_Figur datenFigur,boolean isWeissFeld){
		Image im=null;
		Graphics2D g=null;
		if (datenFigur.getString("position").equals("")){ // Figur ist geschlagen
			im=new BufferedImage(Parameter.groesseFeld/2,Parameter.groesseFeld/2,BufferedImage.TYPE_INT_ARGB);
			g=(Graphics2D)im.getGraphics();
			g.setColor(Parameter.farbeBrettHintergrund);
		}
		else{ // Figur ist auf dem Brett
			im=new BufferedImage(Parameter.groesseFeld,Parameter.groesseFeld,BufferedImage.TYPE_INT_ARGB);
			g=(Graphics2D)im.getGraphics();
			if (!isWeissFeld)
				g.setColor(Parameter.farbeBrettWeiss);
			else 
				g.setColor(Parameter.farbeBrettSchwarz);
		}
		g.fillRect(0,0,im.getHeight(null),im.getWidth(null));
		BufferedImage bildFigur=null;
		FigurEnum figurTyp=FigurEnum.valueOf(datenFigur.getString("typ"));
		if (datenFigur.getBool("isWeiss"))
			bildFigur=figurenWeiss[figurTyp.ordinal()];
		else
			bildFigur=figurenSchwarz[figurTyp.ordinal()];
		if (datenFigur.getString("position").equals(""))
			bildFigur=toBufferedImage(bildFigur.getScaledInstance(bildFigur.getWidth()/2,bildFigur.getHeight()/2,Image.SCALE_SMOOTH));
		g.drawImage(bildFigur,2,2,null);
		g.dispose();
		return im;
	}

	private static BufferedImage toBufferedImage(Image im){
		if (im instanceof BufferedImage) return (BufferedImage) im;
    BufferedImage bimage = new BufferedImage(im.getWidth(null), im.getHeight(null), BufferedImage.TYPE_INT_ARGB);
    Graphics2D bGr = bimage.createGraphics();
    bGr.drawImage(im, 0, 0, null);
    bGr.dispose();
    return bimage;
	}

	private class Feld {
		private boolean isWeiss;
		private D_Figur datenFigur=null;

		public Feld(boolean isWeiss){
			this.isWeiss=isWeiss;
		}
		
		public boolean isWeiss() {
			return isWeiss;
		}

		public Image getBildFeld(){
			if (datenFigur!=null) return getBildFigur(datenFigur,isWeiss);
			Image im=new BufferedImage(Parameter.groesseFeld,Parameter.groesseFeld,BufferedImage.TYPE_INT_RGB);
			Graphics2D g=(Graphics2D)im.getGraphics();
			if (isWeiss())
				g.setColor(Parameter.farbeBrettSchwarz);
			else
				g.setColor(Parameter.farbeBrettWeiss);
			g.fillRect(0,0,im.getHeight(null),im.getWidth(null));
			g.dispose();
			return im;
		}

		public void setDatenFigur(D_Figur datenFigur) {
			this.datenFigur=datenFigur;
		}
	}
}
