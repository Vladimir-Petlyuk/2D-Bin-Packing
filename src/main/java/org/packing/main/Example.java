package main.java.org.packing.main;



import main.java.org.packing.core.Bin;
import main.java.org.packing.core.BinPacking;
import main.java.org.packing.primitives.MArea;
import main.java.org.packing.utils.Utils;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Example {

    public static void main(String[] args) throws IOException {
        Example ex = new Example();

        ex.launch("E://projects/2D-Bin-Packing/src/main/resources/Rectangles.txt");
    }

    private void launch(String fileName) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(fileName));
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        Object[] result = Utils.loadPieces(reader);

        Dimension binDimension = (Dimension) result[0];
        MArea[] pieces = (MArea[]) result[1];

        Bin[] bins = BinPacking.BinPackingStrategy(pieces, binDimension);
        drawbinToFile(bins);
    }

    private void drawbinToFile(Bin[] bins) throws IOException {
        for (int i = 0; i < bins.length; i++) {

            MArea[] areasInThisbin = bins[i].getPlacedPieces();
            List<MArea> areas = new ArrayList<>(Arrays.asList(areasInThisbin));
            Utils.drawMAreasToFile(areas, bins[i].getDimension(), ("Bin-" + String.valueOf(i + 1)));
        }
    }

}
