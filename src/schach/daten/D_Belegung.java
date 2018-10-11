package schach.daten;

public class D_Belegung extends D{
	
	public D_Belegung(){
		// der Zug, der zu dieser Belegung gefuehrt hat
		addString("von","");
		addString("nach","");
		addString("status","");
		addString("bemerkung","");
		// Daten der Belegung
		addInt("anzahlFigurenAufBrett",0);
		addInt("anzahlFigurenGeschlagen",0);
	}
}
