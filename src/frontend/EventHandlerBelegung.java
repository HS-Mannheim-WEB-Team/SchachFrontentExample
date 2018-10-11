package frontend;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import schach.backend.BackendSpielAdminStub;
import schach.backend.BackendSpielStub;
import schach.daten.D;
import schach.daten.D_Figur;
import schach.daten.Xml;

public class EventHandlerBelegung implements ActionListener,MouseListener{
	private Frontend frontend;
	private BackendSpielStub backendSpiel=null;
	private BackendSpielAdminStub backendSpielAdmin=null;
	private String feldMarkiert=null;
	private ArrayList<String> felderErlaubt=new ArrayList<String>();
	
	public EventHandlerBelegung(Frontend frontend){
		this.frontend=frontend;
		backendSpiel=frontend.getBackendSpiel();
		backendSpielAdmin=frontend.getBackendSpielAdmin();
	}
	
	public void reset(){
		this.feldMarkiert=null;
		this.felderErlaubt=new ArrayList<String>();
	}
	
	@Override
	public void actionPerformed(ActionEvent ev) {
		Object quelle=ev.getSource();
		String xml="";
		if (quelle.equals(frontend.mSpielNeu)){
			xml=backendSpielAdmin.neuesSpiel(Parameter.idSpiel);
			frontend.setEnde(false);
			frontend.printlnLog(Xml.toD(xml).getString("meldung"));
		} 
		if (quelle.equals(frontend.mSpielLaden)){
			xml=backendSpielAdmin.ladenSpiel(Parameter.idSpiel,Parameter.pfadSpielServer);
			frontend.printlnLog(Xml.toD(xml).getString("meldung"));
		}
		if (quelle.equals(frontend.mSpielSpeichern)){
			xml=backendSpielAdmin.speichernSpiel(Parameter.idSpiel,Parameter.pfadSpielServer);
			frontend.printlnLog(Xml.toD(xml).getString("meldung"));
		}
		Belegung b=new Belegung(backendSpiel.getAktuelleBelegung(Parameter.idSpiel),frontend.ichSpieleWeiss());
		frontend.setBelegung(b);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (frontend.isInHistorienAnsicht()){
			reset();
			return;
		}
		int x=getKoordinateX(e.getX());
		int y=getKoordinateY(e.getY());
		if ((x==0)||(y==0)) return;
		if ((felderErlaubt!=null)&&felderErlaubt.contains(Frontend.toSchachNotation(x,y))){
			if (!frontend.ichBinAmZug()){
				frontend.log("Sie sind nicht am Zug!");
				return;
			}
			// 2. Klick auf ein Feld: Zug durchfuehren
			backendSpiel.ziehe(Parameter.idSpiel,feldMarkiert,Frontend.toSchachNotation(x,y));	
			Belegung b=new Belegung(backendSpiel.getAktuelleBelegung(Parameter.idSpiel),frontend.ichSpieleWeiss());
			frontend.setBelegung(b); 
			reset();
		}
		else{
	 		D_Figur d_Figur=null;
			D d=Xml.toD(backendSpiel.getFigur(Parameter.idSpiel,Frontend.toSchachNotation(x,y)));
			if (d instanceof D_Figur) d_Figur=(D_Figur)d;
			if ((d_Figur==null)||(frontend.ichSpieleWeiss()!=d_Figur.getBool("isWeiss"))){
				// das Feld hat keine Figur oder eine Figur der anderen Farbe -> ich darf nicht ziehen.
				frontend.markiereFelder(x,y,null);
				this.feldMarkiert=Frontend.toSchachNotation(x,y);
				this.felderErlaubt=null;
			}
			else{
				// das Feld hat eine Figur -> moegliche Zuege ermitteln
				String xml=backendSpiel.getErlaubteZuege(Parameter.idSpiel,Frontend.toSchachNotation(x,y));
				ArrayList<D> d_erlaubteZuege=Xml.toArray(xml);
				ArrayList<String> sFelderErlaubt=new ArrayList<String>();
				for(D d_Zug:d_erlaubteZuege){
					sFelderErlaubt.add(d_Zug.getString("nach"));
				}
				frontend.markiereFelder(x,y,sFelderErlaubt);
				this.feldMarkiert=Frontend.toSchachNotation(x,y);
				this.felderErlaubt=sFelderErlaubt;
			}
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	private int getKoordinateX(int x){
		int start=20;
		int offset=50;
		for (int i=1;i<=8;i++){
			if ((x>=start)&&(x<=start+50)){
				if (frontend.ichSpieleWeiss())
					return i;
				else
					return 9-i;
			}
			start+=offset;
		}			
		return 0;
	}

	private int getKoordinateY(int y){
		int start=472;
		int offset=50;
		for (int i=1;i<=8;i++){
			if ((y<=start)&&(y>=start-50))
				if (frontend.ichSpieleWeiss())
					return i;
				else
					return 9-i;
			start-=offset;
		}
		return 0;
	}
}
