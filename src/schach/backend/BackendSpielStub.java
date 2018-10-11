package schach.backend;

import java.util.ArrayList;

import javax.ws.rs.client.Client;

import frontend.Parameter;
import interfaces.iBackendSpiel;
import schach.daten.D;
import schach.daten.Xml;
import utils.BackendUtils;

public class BackendSpielStub implements iBackendSpiel {
	private static final String urlUnterPfad = "schach/spiel/";
	private String url;
	private Client client;

	public BackendSpielStub(String url) {
		if (url.endsWith("/"))
			this.url = url + urlUnterPfad;
		else
			this.url = url + "/" + urlUnterPfad;

		try {
			this.client = BackendUtils.ignoreSSLClient();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getXmlvonRest(String pfad) {
		try{
			String anfrage = url + pfad;
			if (Parameter.log)
				System.out.println("CLIENT ANFRAGE: " + anfrage);
			String s = client.target(anfrage).request().accept("application/xml").get(String.class);
			if (Parameter.log) {
				ArrayList<D> daten = Xml.toArray(s);
				System.out.println(daten);
			}
			return s;			
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			return "";
		}
	}

	@Override
	public String getSpielDaten(int id) {
		return getXmlvonRest("getSpielDaten/"+id);
	}

	@Override
	public String getAktuelleBelegung(int id) {
		return getXmlvonRest("getAktuelleBelegung/"+id);
	}

	@Override
	public String getBelegung(int id,int nummer) {
		return getXmlvonRest("getBelegung/"+id+"/"+nummer);
	}

	@Override
	public String getAlleErlaubtenZuege(int id) {
		return getXmlvonRest("getAlleErlaubtenZuege/"+id);
	}

	@Override
	public String getFigur(int id,String position) {
		return getXmlvonRest("getFigur/"+id+"/"+position);
	}

	@Override
	public String getErlaubteZuege(int id,String position) {
		return getXmlvonRest("getErlaubteZuege/"+id+"/"+position);
	}

	@Override
	public String ziehe(int id,String von, String nach) {
		return getXmlvonRest("ziehe/"+id+"/"+von+"/"+nach);
	}

	@Override
	public String getZugHistorie(int id) {
		return getXmlvonRest("getZugHistorie/"+id);
	}
}
