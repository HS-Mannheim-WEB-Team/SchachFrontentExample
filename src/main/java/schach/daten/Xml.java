package schach.daten;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import frontend.Parameter;

public class Xml {
	public static final String xmlHeaderProperty="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">\n";
	public static final String xmlHeaderArray="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<!DOCTYPE properties SYSTEM \"http://www.frankdopatka.de/dtd/propertiesarray.dtd\">\n<propertiesarray>\n";
	public static final String xmlFooterArray="</propertiesarray>\n";
	private static Pattern pattern01=Pattern.compile("<properties>.*?</properties>",Pattern.DOTALL);
	private static Pattern pattern02=Pattern.compile("</properties>"+System.lineSeparator()+"<properties>",Pattern.DOTALL);

	public static D toD(String xml){
		try{
			if (!xml.startsWith("<?xml")) xml=xmlHeaderProperty+xml;
			Properties p=new Properties();
			p.loadFromXML(new ByteArrayInputStream(xml.getBytes()));
			@SuppressWarnings("rawtypes")
			Class c=Class.forName(Parameter.pfadKlassenDaten+p.getProperty("klasse"));
			D d=(D)c.newInstance();
			d.setProperties(p);
			return d;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String fromD(D d){
		try {
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			d.getProperties().storeToXML(baos,null);
			String s=baos.toString();
			String[] zeilen=s.split("\n");
			StringBuffer xml=new StringBuffer();
			for (int i=2;i<zeilen.length;i++) xml.append(zeilen[i]+"\n");
			return xml.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Xml fromD: "+e.getMessage());
		}
	}
	
	public static String fromArray(ArrayList<D> datenArray){
		try{
			StringBuffer xml=new StringBuffer();
			if ((datenArray!=null)&&(datenArray.size()>0)){
				for(D d:datenArray) xml.append(Xml.fromD(d));
			}
			return xml.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Xml fromArray: "+e.getMessage());
		}
	}

	public static ArrayList<D> toArray(String xml){
		try{
			ArrayList<D> datenArray=new ArrayList<D>();
			Matcher m=pattern01.matcher(xml); 
			while (m.find()){
				String xmlTeil=xmlHeaderProperty+m.group();
				xmlTeil=xmlTeil.replaceAll("/n","");
				Properties p=new Properties();
				p.loadFromXML(new ByteArrayInputStream(xmlTeil.getBytes()));
				@SuppressWarnings("rawtypes") 
				Class c=Class.forName(Parameter.pfadKlassenDaten+p.getProperty("klasse"));
				D d=(D)c.newInstance();
				d.setProperties(p);
				datenArray.add(d);
			}
			return datenArray;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Xml toArray: "+e.getMessage());
		}
	}
	
	public static String verpacken(String xml){
		StringBuffer xmlVersenden=new StringBuffer();
		String[] teile=xml.split(pattern02.toString(),Pattern.DOTALL);
		if (teile.length>1){ // mehrere Properties-Objekte im Xml
			xmlVersenden.append(xmlHeaderArray);
			xmlVersenden.append(xml);
			xmlVersenden.append(xmlFooterArray);
		}
		else{  // nur genau 1 Properties-Objekt im Xml
			xmlVersenden.append(xmlHeaderProperty);
			xmlVersenden.append(xml);
		}
		return xmlVersenden.toString();
	}
	
	public static String verpackeFehler(Exception e){
		return Xml.verpacken(Xml.fromD(new D_Fehler(e.getMessage())));
	}
	
	public static String verpackeOK(String meldung){
		return Xml.verpacken(Xml.fromD(new D_OK(meldung)));
	}
}
