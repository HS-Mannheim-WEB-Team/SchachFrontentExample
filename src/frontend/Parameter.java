package frontend;


import java.awt.Color;

public class Parameter {

	public static final int idSpiel=1;
	public static final boolean log = true;
	
	public static final String zumServer="http://www.game-engineering.de:8080/rest/";
	
	public static final String pfadSpielServer="/home/dopatka02/Repo-GE/Game-Engineering/schach.spiel/spiel.xml";
	
	public static final String pfadKlassenDaten="schach.daten.";
	public static final String pfadFiguren="figuren//";
	public static final String endungFiguren=".gif";
	
	public static final Color farbeBrettWeiss=new Color(230,230,230);
	public static final Color farbeBrettSchwarz=new Color(238,154,73);
	public static final Color farbeBrettHintergrund=new Color(0,190,0);
	
	public static final int groesseFeld=50;
}
