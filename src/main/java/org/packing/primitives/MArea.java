package main.java.org.packing.primitives;



import java.awt.*;
import java.awt.geom.*;
import java.util.Comparator;

public class MArea extends Area {
    /**
     * Area measure.
     */
    private double area;

    private int ID;
    /**
     * Accumulate rotation in degrees of this MArea
     */
    private double rotation;

    /**
     * Creates an MArea based on a Path2D previously constructed
     *
     * @param path Path2D of this MAreq previously constructed
     * @param ID   identification for this MArea
     */
    public MArea(Path2D path, int ID) {
        super(path);
        this.ID = ID;
        rotation = 0;
    }

    /**
     * Creates an MArea based on a MArea previously constructed
     *
     * @param area MArea from which we are going to construct this MArea
     * @param ID   identification for this MArea
     */
    public MArea(MArea area, int ID) {
        super(area);
        this.area = area.area;
        this.ID = ID;
        rotation = area.getRotation();
    }

    /**
     * Creates an MArea based on a Rectangle previously constructed
     *
     * @param rectangle from which we are going to construct this MArea
     * @param ID        identification for this MArea
     * @see Rectangle
     */
    public MArea(Rectangle rectangle, int ID) {
        super(rectangle);
        this.area = rectangle.getWidth() * rectangle.getHeight();
        this.ID = ID;
        rotation = 0;
    }

    /**
     * Creates an MArea based on a double precision Rectangle previously
     * constructed
     *
     * @param rectangle from which we are going to construct this MArea
     * @param ID        identification for this MArea
     * @see Rectangle2D.Double
     */
    public MArea(Rectangle2D.Double rectangle, int ID) {
        super(rectangle);
        this.area = rectangle.getWidth() * rectangle.getHeight();
        this.ID = ID;
        rotation = 0;
    }

    /**
     * Creates an empty MArea with an ID.
     *
     * @param ID
     */
    public MArea(int ID) {
        super();
        this.area = 0;
        this.ID = ID;
        rotation = 0;
    }

    /**
     * Creates an empty area without ID.
     */
    public MArea() {
        super();
        this.area = 0;
        rotation = 0;
    }

    /**
     * Creates an MArea with a hole.
     *
     * @param outer MArea describing the outer MArea of this piece
     * @param inner MArea describing the inner MArea of this piece
     */
    public MArea(MArea outer, MArea inner) {
        super(MAreaHolesConstructor(outer, inner));
        this.ID = outer.getID();
        rotation = 0;
    }

    private static MArea MAreaHolesConstructor(MArea outer, MArea inner) {
        MArea area = new MArea(outer, outer.getID());
        area.subtract(inner);
        return area;
    }


    /**
     * Method for calculating the bounding box of this MArea in integer
     * precision
     *
     * @return Bounding box rectangle of this MArea in integer precision.
     */
    public Rectangle getBoundingBox() {
        return (Rectangle) this.getBounds();
    }

    /**
     * Method for calculating the bounding box of this MArea in double precision
     *
     * @return Bounding box rectangle of this MArea in double precision.
     * @see Rectangle2D.Double
     */
    public Rectangle2D.Double getBoundingBox2D() {
        return (Rectangle2D.Double) this.getBounds2D();
    }


    /**
     * @return area measure of this MArea
     */
    public double getArea() {
        return area;
    }


    /**
     * @return ID of this MArea
     */
    public int getID() {
        return ID;
    }

    /**
     * Equality test based on ID
     *
     * @return true if ID's are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        MArea other = (MArea) obj;
        return this.ID == other.ID;
    }

    /**
     * Places this MArea's bounding box upper left corner in x,y
     *
     * @param x
     * @param y
     */
    public void placeInPosition(double x, double y) {
        Rectangle bb = getBoundingBox();
        AffineTransform transform = new AffineTransform();
        double thisX = bb.getX();
        double dx = Math.abs(thisX - x);
        if (thisX <= x) {
            thisX = dx;
        } else {
            thisX = -dx;
        }
        double thisY = bb.getY();
        double dy = Math.abs(thisY - y);
        if (thisY <= y) {
            thisY = dy;
        } else {
            thisY = -dy;
        }
        transform.translate(thisX, thisY);
        this.transform(transform);
    }

    /**
     * Rotates this area, in degrees.
     *
     * @param degrees to rotate the piece
     */
    public void rotate(double degrees) {
        this.rotation += degrees;
        if (this.rotation >= 360) {
            this.rotation -= 360;
        }
        AffineTransform transform = new AffineTransform();
        Rectangle rectangle = getBoundingBox();
        transform.rotate(Math.toRadians(degrees), rectangle.getX() + rectangle.width / 2, rectangle.getY() + rectangle.height / 2);
        this.transform(transform);
    }

    /**
     * Verifies if this area intersects with other area
     *
     * @param other
     * @return true if intersects, false otherwise
     */

    public boolean intersection(MArea other) {
        MArea intersArea = new MArea(this, this.ID);
        intersArea.intersect(other);
        return !intersArea.isEmpty();
    }


    public double getRotation() {
        return rotation;
    }

    public static final Comparator<MArea> BY_AREA = new ByArea();


    /**
     * Provides an area based comparison between two MAreas. It is assumed that
     * the area measure is up to date at the time of this comparison
     */
    private static class ByArea implements Comparator<MArea> {
        @Override
        public int compare(MArea o1, MArea o2) {
            return Double.compare(o1.area, o2.area);
        }
    }

}
