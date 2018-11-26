package main.java.org.packing.core;




import main.java.org.packing.primitives.MArea;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BinPacking {
	/**
	 * Entry point for the application. Applies the packing strategies to the
	 * provided pieces.
	 *
	 * @param pieces            pieces to be nested inside the bins.
	 * @param binDimension      dimensions for the generated bins.
	 * @return list of generated bins.
	 */
	public static Bin[] BinPackingStrategy(MArea[] pieces, Dimension binDimension) {
		List<Bin> bins = new ArrayList<>();
		boolean stillToPlace = true;
		MArea[] notPlaced = pieces;
		while (stillToPlace) {
			stillToPlace = false;
			Bin bin = new Bin(binDimension);
			notPlaced = bin.BBCompleteStrategy(notPlaced);

			bins.add(bin);
			if (notPlaced.length > 0)
				stillToPlace = true;
		}
		return bins.toArray(new Bin[0]);
	}

}
