package schach.daten;

public enum ZugEnum {
	RochadeKurz,RochadeLang,EnPassant,BauerDoppelschritt,BauerUmgewandelt;
	
	public static ZugEnum toEnumFromString(String s){
		if ((s==null)||(s.length()==0)) return null;
		return valueOf(s);
	}
}
