package interfaces;

public interface iBackendSpielAdmin {
	// id: ID des Spiels
	String neuesSpiel(int id);
	String speichernSpiel(int id, String pfad);
	String ladenSpiel(int id, String pfad);
}
