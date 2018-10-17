package interfaces;

public interface iBackendSpiel {
	// id: ID des Spiels
	String getAktuelleBelegung(int id);
	String getBelegung(int id, int nummer);
	String getErlaubteZuege(int id, String position);
	String ziehe(int id, String von, String nach);
	String getSpielDaten(int id);
	String getAlleErlaubtenZuege(int id);
	String getFigur(int id, String position);
	String getZugHistorie(int id);
}