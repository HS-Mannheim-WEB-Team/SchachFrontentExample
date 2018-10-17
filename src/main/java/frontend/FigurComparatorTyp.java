package frontend;

import java.util.Comparator;

import schach.daten.D_Figur;
import schach.daten.FigurEnum;

public class FigurComparatorTyp implements Comparator<D_Figur> {
	@Override
	public int compare(D_Figur figur1, D_Figur figur2) {
		FigurEnum typ1=FigurEnum.toEnumFromString(figur1.getString("typ"));
		FigurEnum typ2=FigurEnum.toEnumFromString(figur2.getString("typ"));
		typ1.ordinal();
		return typ1.ordinal()-typ2.ordinal();
	}
}
