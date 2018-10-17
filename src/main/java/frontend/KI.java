package frontend;

import schach.backend.BackendSpielStub;
import schach.daten.D;
import schach.daten.D_Spiel;
import schach.daten.SpielEnum;
import schach.daten.Xml;

public abstract class KI extends Thread{
	private String info;
	private boolean binWeiss;
	private BackendSpielStub backendSpiel;
	private int pause=1000;
	private boolean ende=false;
	private Frontend frontend;

	public KI(String info) {
		this.info=info;
	}

	public void init(boolean binWeiss, BackendSpielStub backendSpiel){
		this.binWeiss=binWeiss;
		this.backendSpiel=backendSpiel;
	}
	
	public String getInfo() {
		return info;
	}
	
	public boolean binWeiss(){
		return binWeiss;
	}
	public boolean binSchwarz(){
		return !binWeiss();
	}
	
	public BackendSpielStub getBackend(){
		return backendSpiel;
	}
	
	public void schlafen(int ms){
		try {
			Thread.sleep(ms);
		} catch (Exception e) {}
	}

	@Override
	public void start() {
		super.start();		
	}

	@Override
	public void run(){
		while (!ende){
			try {
				D d=Xml.toD(getBackend().getSpielDaten(Parameter.idSpiel));
				D_Spiel d_Spiel=(D_Spiel)d;
				String status=""+d_Spiel.getString("status");
				boolean weissMatt=status.equals(""+SpielEnum.WeissSchachMatt);
				boolean schwarzMatt=status.equals(""+SpielEnum.SchwarzSchachMatt);
				boolean patt=status.equals(""+SpielEnum.Patt);

				if (weissMatt||schwarzMatt||patt){
					// Spiel ist zu Ende
					if (binWeiss()&&weissMatt) 
						ichHabeVerloren();
					else if (binSchwarz()&&schwarzMatt) 
						ichHabeVerloren();
					else if (patt) 
						patt();
					else
						ichHabeGewonnen();
					ende=true;
				}
				else{
					// Spiel geht weiter
					if ((d_Spiel.getInt("anzahlZuege")%2==0)==binWeiss())
						ichBinAmZug(d_Spiel);
					else
						ichBinNichtZug(d_Spiel);					
					schlafen(pause);				
				}
			} catch (Exception e) {
				schlafen(pause);				
			} 
		}
	}
	
	public abstract void ichBinAmZug(D_Spiel d_Spiel);
	
	public abstract void ichBinNichtZug(D_Spiel d_Spiel);	

	public abstract void ichHabeVerloren();	

	public abstract void ichHabeGewonnen();	

	public abstract void patt();

	public void setFrontend(Frontend frontend) {
		this.frontend=frontend;
	}
	
	public Frontend getFrontend() {
		return frontend;
	}
}