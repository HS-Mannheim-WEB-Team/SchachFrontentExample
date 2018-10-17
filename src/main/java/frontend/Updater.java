package frontend;

import schach.backend.BackendSpielStub;
import schach.daten.D_Spiel;
import schach.daten.Xml;

public class Updater extends Thread{
	private Frontend frontend;
	private BackendSpielStub backendSpiel;
	private int timer;
	
	public Updater(Frontend frontend,int timer){
		this.frontend=frontend;
		backendSpiel=frontend.getBackendSpiel();
		this.timer=timer;
		this.start();
	}
	
	@Override
	public void run(){
		while(true){
			try{
				D_Spiel d_spiel=(D_Spiel)Xml.toD(backendSpiel.getSpielDaten(Parameter.idSpiel));
				if (frontend.getAnzahlZuege()!=d_spiel.getInt("anzahlZuege")){
					update(d_spiel);
				}
			}
			catch (Exception e){}
			try {
				Thread.sleep(timer*2000);
			} catch (InterruptedException e) {}
		}
	}
	
	public void update(D_Spiel d_spiel){
		int anzahlZuege=d_spiel.getInt("anzahlZuege");
		Belegung b=new Belegung(backendSpiel.getAktuelleBelegung(Parameter.idSpiel),frontend.ichSpieleWeiss());
		frontend.setBelegung(b); 
		if (b.isWeissSchachMatt()||b.isSchwarzSchachMatt()||b.isPatt()){
			// Spiel ist zu Ende
			if (frontend.ichSpieleWeiss()&&b.isWeissSchachMatt()) 
				frontend.log("Leider verloren, Weiss ist SCHACH MATT :-(");
			else if (frontend.ichSpieleSchwarz()&&b.isSchwarzSchachMatt()) 
				frontend.log("Leider verloren, Schwarz ist SCHACH MATT :-(");
			else if (b.isPatt()) 
				frontend.log("UNENDSCHIEDEN! PATT!");
			else
				frontend.log("GEWONNEN! Dein Gegner ist SCHACH MATT!");
			frontend.setEnde(true);
		}else{
			if (frontend.ichSpieleWeiss()&&b.isWeissImSchach()){
				frontend.log("SCHACH!");
			}else if (frontend.ichSpieleSchwarz()&&b.isSchwarzImSchach()){
				frontend.log("SCHACH!");
			}else if (frontend.ichSpieleWeiss()&&b.isSchwarzImSchach()){
				frontend.log("Dein Gegner ist im SCHACH!");
			}else if (frontend.ichSpieleSchwarz()&&b.isWeissImSchach()){
				frontend.log("Dein Gegner ist im SCHACH!");
			}
		}
		frontend.setAnzahlZuege(anzahlZuege);
		frontend.setHistorienAnsicht(false);
		frontend.updateLog();
	}
}
