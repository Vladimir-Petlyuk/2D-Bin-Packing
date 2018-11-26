package main.java.org.packing.utils;



import main.java.org.packing.primitives.MArea;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;


public class Utils {

    /**
     * Takes two rectangles and check if the first fits into the second
     *
     * @param o1 rectangle to be contained
     * @param o2 rectangle to be the container
     * @return true if o1 fits into o2, false otherwise
     * @see Rectangle2D
     */
    public static boolean fits(Rectangle2D o1, Rectangle2D o2) {
        return (o1.getHeight() <= o2.getHeight() && o1.getWidth() <= o2.getWidth());
    }

    /**
     * Takes two rectangles and check if the rotation of the first (90�) fits
     * into the other
     *
     * @param o1 rectangle to be rotated 90� and checked
     * @param o2 rectangle container
     * @return true if a 90� rotation of the first fits into the second, false
     * otherwise
     * @see Rectangle2D
     */
    public static boolean fitsRotated(Rectangle2D o1, Rectangle2D o2) {
        return (o1.getHeight() <= o2.getWidth() && o1.getWidth() <= o2.getHeight());
    }


    /**
     * Draws a list of pieces taking into account the bin dimension and the
     * desired viewport dimension
     *
     * @param pieces            pieces to be drawn
     * @param binDimension      bin real Dimension
     * @param name              name of the file to be drawn
     * @throws IOException if a problem occurs during the creation of the file
     * @see Dimension
     * @see IOException
     */
    public static void drawMAreasToFile(List<MArea> pieces, Dimension binDimension, String name) throws IOException {
        BufferedImage img = new BufferedImage(binDimension.width + 20, binDimension.height + 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setStroke(new BasicStroke(10.0f));
        g2d.setColor(Color.BLACK);

        g2d.drawRect(0, 0, binDimension.width, binDimension.height);

        g2d.setColor(Color.red);
        for (MArea piece : pieces) {
            g2d.draw(piece.getBounds());
        }
        File outputFile = new File(name + ".png");
        ImageIO.write(img, "png", outputFile);
    }



    /**
     * @param reader
     * @return @Object[] that contains the specified bin dimension, the
     * calculated viewport dimension and the pieces read from file:
     * position 0 - (Dimension)binDimension position 1 -
     * (Dimension)viewPortDimension position 2 - (MArea[])pieces
     * @throws IOException
     */
    public static Object[] loadPieces(BufferedReader reader) throws IOException {
        Scanner sc = new Scanner(reader);
        return readInput(sc);
    }

    /**
     * From a scanner, reads the pieces.
     *
     * @return
     */
    private static Object[] readInput(Scanner sc) {
        Dimension binDimension = new Dimension(sc.nextInt(), sc.nextInt());

        int N = sc.nextInt();
        sc.nextLine();
        MArea[] pieces = new MArea[N];
        int n = 0;
        while (n < N) {
            String s = sc.nextLine();
            String[] src = s.split("\\s+");

            double width = Double.valueOf(src[0]);
            double height = Double.valueOf(src[1]);

            pieces[n] = new MArea(new Rectangle2D.Double(0, 0, width, height), n);
            ++n;
        }
        sc.close();
        return new Object[] {binDimension, pieces};
    }

}
