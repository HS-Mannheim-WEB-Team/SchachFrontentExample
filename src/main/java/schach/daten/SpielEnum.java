package schach.daten;

public enum SpielEnum {
	WeissImSchach,SchwarzImSchach,WeissSchachMatt,SchwarzSchachMatt,Patt;
	
	public static SpielEnum fromString(String s){
		if ((s==null)||(s.length()==0)) return null;
		return valueOf(s);
	}
}
