package schach.daten;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;

// Basis-Datencontainer
public class D{
	private Properties p=new Properties();

	public D() {
		addString("klasse",""+this.getClass().getSimpleName());
	}

	public D(Properties p) {
		this();
		if (p==null)
			throw new RuntimeException("D Konstruktor: Properties duerfen nicht NULL sein!");
		this.p=p;
	}

	public static int toInt(String s){
		return Integer.parseInt(s);
	}

	public void addString(String name,String wert){
		if (wert==null) wert="";
		p.setProperty(name,wert);
	}
	public void addInt(String name,int wert){
		p.setProperty(name,""+wert);
	}
	public void addBool(String name,boolean wert){
		p.setProperty(name,""+wert);
	}
	public void addDecimal(String name,String wert){
		if (wert==null) wert="0.0";
		p.setProperty(name,wert);
	}

	public void setString(String name,String wert){
		if (wert==null) wert="";
		p.setProperty(name,wert);
	}
	public void setInt(String name,int wert){
		p.setProperty(name,""+wert);
	}
	public void incInt(String name){
		if (!existKey(name))
			throw new RuntimeException("Daten incInt: Attribut existiert nicht!");
		int wert=Integer.parseInt(p.getProperty(name));
		wert++;
		p.setProperty(name,""+wert);
	}
	public void decInt(String name){
		if (!existKey(name))
			throw new RuntimeException("Daten decInt: Attribut existiert nicht!");
		int wert=Integer.parseInt(p.getProperty(name));
		wert--;
		p.setProperty(name,""+wert);
	}

	public void setBool(String name,boolean wert){
		p.setProperty(name,""+wert);
	}
	public void invertBool(String name){
		if (!existKey(name))
			throw new RuntimeException("Daten invertBool: Attribut existiert nicht!");
		boolean wert=getBool(name);
		p.setProperty(name,""+(!wert));
	}
	public void setDecimal(String name,String wert){
		if (wert==null) wert="0.0";
		p.setProperty(name,wert);
	}
		
	public boolean existKey(String name){
		return p.containsKey(""+name);
	}

	public boolean existValue(String name){
		return p.containsValue(""+name);
	}

	public String getString(String name){
		return p.getProperty(name);
	}
	public int getInt(String name){
		int ergebnis=0;
		try{
			ergebnis=Integer.parseInt(p.getProperty(name));			
		}
		catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException("Daten getInt: "+e.getMessage());
		}
		return ergebnis;
	}
	public boolean getBool(String name){
		boolean ergebnis=false;
		try{
			ergebnis=Boolean.parseBoolean(p.getProperty(name));			
		}
		catch (Exception e){
			throw new RuntimeException("Daten getBool: "+e.getMessage());
		}
		return ergebnis;
	}
	public BigDecimal getDecimal(String name){
		BigDecimal ergebnis=new BigDecimal(0);
		try{
			ergebnis=new BigDecimal(p.getProperty(name));			
		}
		catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException("Daten getDecimal: "+e.getMessage());
		}
		return ergebnis;
	}
	
		public ArrayList<String> getAttribute(){
		Set<String> keys=p.stringPropertyNames();
		ArrayList<String> ergebnis=new ArrayList<String>();
		ergebnis.addAll(keys);
		return ergebnis;
	}
	
	public ArrayList<String> getListe(){
		ArrayList<String> ergebnis=new ArrayList<String>();
		int i=0;
		while (existKey(""+i)){
			ergebnis.add(getString(""+i));
			i++;
		}
		return ergebnis;
	}
		
	public void setProperties(Properties p){
		if (p==null)
			throw new RuntimeException("D Konstruktor: Properties duerfen nicht NULL sein!");
		this.p=p;
	}
	public Properties getProperties(){
		return p;
	}

	@Override
	public String toString(){
		return p.toString();
	}
	
	public boolean equals(Object o){
		if (o==null) return false;
		if (!this.getClass().getSimpleName().equals(o.getClass().getSimpleName())) return false;
		D d=(D)o;
		return this.p.equals(d.p);
	}
	
	@Override
	public D clone(){
		try {
			@SuppressWarnings("unchecked")
			Class<D> c=(Class<D>)Class.forName(this.getClass().getName());
			D d=(D)c.newInstance();
			d.p=(Properties)this.p.clone();
			return d;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Daten clone: "+e.getMessage());
		}
	}
	
	
	public String toXml(){
		return(Xml.fromD(this));
	}
	
}
