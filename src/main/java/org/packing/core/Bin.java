package main.java.org.packing.core;

import main.java.org.packing.primitives.MArea;
import main.java.org.packing.utils.Utils;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class Bin {
	/**
	 * Bin dimensions.
	 */
	private Dimension dimension;

	/**
	 * Pieces contained in the bin.
	 */
	private MArea[] placedPieces;

	/**
	 * Rectangular holes in the bin
	 */
	private List<Rectangle2D.Double> freeRectangles = new ArrayList<java.awt.geom.Rectangle2D.Double>();

	/**
	 * Initializes this bin with the specified dimensions.
	 *
	 * @param dimension dimensions for this bin.
	 */
	public Bin(Dimension dimension) {
		this.dimension = new Dimension(dimension.width, dimension.height);
		freeRectangles.add(new Rectangle2D.Double(0, 0, dimension.getWidth(), dimension.getHeight()));
	}

	/**
	 * Get the placed pieces.
	 *
	 * @return placed pieces.
	 */
	public MArea[] getPlacedPieces() {
		return placedPieces;
	}

	/**
	 * Get the dimensions.
	 *
	 * @return dimensions.
	 */
	public Dimension getDimension() {
		return dimension;
	}


	/**
	 * Performs the complete bounding box based strategies to place the pieces
	 * inside this bin.
	 *
	 * @param toPlace pieces to be placed inside this bin.
	 * @return the pieces that could not be placed inside the bin.
	 */
	public MArea[] BBCompleteStrategy(MArea[] toPlace) {
		return boundingBoxPacking(toPlace);
	}

	/**
	 * Places the pieces inside the bin using the maximal rectangles strategy.
	 * Method called from {@link #BBCompleteStrategy}
	 *
	 * @param pieces pieces to be placed.
	 * @return the pieces that could not be placed inside the bin.
	 */
	private MArea[] boundingBoxPacking(MArea[] pieces) {
		List<MArea> placedPieces = new ArrayList<>();
		List<MArea> notPlacedPieces = new ArrayList<>();

		Arrays.sort(pieces, MArea.BY_AREA);

		MArea total = new MArea();
		if (this.placedPieces != null) {
			for (MArea a : this.placedPieces) {
				total.add(a);
				placedPieces.add(a);
			}
		}

		for (int i = pieces.length - 1; i >= 0; i--) {
			int where = findWhereToPlace(pieces[i], freeRectangles);
			if (where != -1) {
				Rectangle2D.Double freeRect = freeRectangles.get(where);
				MArea placed = new MArea(pieces[i], pieces[i].getID());
				placed.placeInPosition(freeRect.getX(), freeRect.getMaxY() - placed.getBoundingBox().getHeight());
				if (!placed.intersection(total)) {
					Rectangle2D.Double pieceBB = placed.getBoundingBox2D();
					splitScheme(freeRect, pieceBB, freeRectangles);
					computeFreeRectangles(pieceBB, freeRectangles);
					eliminateNonMaximal();
					placedPieces.add(placed);
					total.add(placed);
				} else {
					notPlacedPieces.add(pieces[i]);
				}
			} else {
				notPlacedPieces.add(pieces[i]);
			}
		}

		this.placedPieces = placedPieces.toArray(new MArea[0]);
		return notPlacedPieces.toArray(new MArea[0]);

	}

	/**
	 * Finds in which free rectangular space the specified piece can be placed.
	 * Method called from {@link #boundingBoxPacking}
	 *
	 * @param piece          piece to place inside an empty rectangular space.
	 * @param freeRectangles list of empty rectangular spaces.
	 * @return <ul>
	 * <li><b>-1</b> if not valid position was found.</li>
	 * <li>
	 * <b>position</b> where the piece can be placed, otherwise.</li>
	 * </ul>
	 */
	private int findWhereToPlace(MArea piece, List<Rectangle2D.Double> freeRectangles) {
		boolean lastRotated = false;
		Rectangle2D pieceBB = piece.getBoundingBox2D();
		int res = -1;
		double min = Double.MAX_VALUE;
		for (int i = freeRectangles.size() - 1; i >= 0; i--) {
			Rectangle2D.Double freeRect = freeRectangles.get(i);
			if (Utils.fits(pieceBB, freeRect)) {
				double m = Math.min(freeRect.getWidth() - pieceBB.getWidth(), freeRect.getHeight() - pieceBB.getHeight());
				if (m < min) {
					min = m;
					res = i;
					if (lastRotated) {
						piece.rotate(90);
						lastRotated = false;
					}

				}
			}
			if (Utils.fitsRotated(pieceBB, freeRect)) {
				double m = Math.min(freeRect.getWidth() - pieceBB.getHeight(), freeRect.getHeight() - pieceBB.getWidth());
				if (m < min) {
					min = m;
					res = i;
					if (!lastRotated) {
						piece.rotate(90);
						lastRotated = true;
					}
				}
			}
		}
		return res;
	}

	/**
	 * Divides the rectangular space where a piece was just placed following the
	 * maximal rectangles splitting strategy. Method called from
	 * {@link #boundingBoxPacking}
	 *
	 * @param usedFreeArea      rectangular area that contains the newly placed piece.
	 * @param justPlacedPieceBB bounding box of the newly placed piece.
	 * @param freeRectangles    list of free spaces in the bin.
	 */
	private void splitScheme(Rectangle2D.Double usedFreeArea, Rectangle2D.Double justPlacedPieceBB, List<Rectangle2D.Double> freeRectangles) {
		freeRectangles.remove(usedFreeArea);
		// top
		double widht = usedFreeArea.getWidth();
		double height = justPlacedPieceBB.getY() - usedFreeArea.getY();
		if (height > 0) {
			Rectangle2D.Double upR = new Rectangle2D.Double(usedFreeArea.getX(), usedFreeArea.getY(), widht, height);
			freeRectangles.add(upR);
		}
		// right
		widht = usedFreeArea.getMaxX() - justPlacedPieceBB.getMaxX();
		height = usedFreeArea.getHeight();
		if (widht > 0) {
			Rectangle2D.Double rightR = new Rectangle2D.Double(justPlacedPieceBB.getMaxX(), usedFreeArea.getY(), widht, height);
			freeRectangles.add(rightR);
		}
	}

	/**
	 * Recalculates the free rectangular boxes in the bin. Method called after a
	 * piece has been placed. Method called from {@link #boundingBoxPacking}
	 *
	 * @param justPlacedPieceBB bounding box of the piece that was just added.
	 * @param freeRectangles    free rectangular boxes in the bin.
	 */
	private void computeFreeRectangles(Rectangle2D.Double justPlacedPieceBB, List<Rectangle2D.Double> freeRectangles) {
		Rectangle2D.Double[] rects = freeRectangles.toArray(new Rectangle2D.Double[0]);
		for (Rectangle2D.Double freeR : rects) {
			if (freeR.intersects(justPlacedPieceBB)) {
				freeRectangles.remove(freeR);
				Rectangle2D rIntersection = freeR.createIntersection(justPlacedPieceBB);
				// top
				double widht = freeR.getWidth();
				double height = rIntersection.getY() - freeR.getY();
				if (height > 0) {
					Rectangle2D.Double upR = new Rectangle2D.Double(freeR.getX(), freeR.getY(), widht, height);
					freeRectangles.add(upR);
				}

				// left
				widht = rIntersection.getX() - freeR.getX();
				height = freeR.getHeight();
				if (widht > 0) {
					Rectangle2D.Double leftR = new Rectangle2D.Double(freeR.getX(), freeR.getY(), widht, height);
					freeRectangles.add(leftR);
				}

				// bottom
				widht = freeR.getWidth();
				height = freeR.getMaxY() - rIntersection.getMaxY();
				if (height > 0) {
					Rectangle2D.Double bottomR = new Rectangle2D.Double(freeR.getX(), rIntersection.getMaxY(), widht, height);
					freeRectangles.add(bottomR);
				}

				// right
				widht = freeR.getMaxX() - rIntersection.getMaxX();
				height = freeR.getHeight();
				if (widht > 0) {
					Rectangle2D.Double rightR = new Rectangle2D.Double(rIntersection.getMaxX(), freeR.getY(), widht, height);
					freeRectangles.add(rightR);
				}
			}
		}

	}

	/**
	 * Eliminates all non-maximal boxes from the empty spaces in the bin. Method
	 * called from {@link #boundingBoxPacking}
	 */
	private void eliminateNonMaximal() {
		Rectangle2D.Double[] freeRectArray = freeRectangles.toArray(new Rectangle2D.Double[0]);
		Arrays.sort(freeRectArray, RECTANGLE_AREA_COMPARATOR);
		freeRectangles.clear();
		for (int i = 0; i < freeRectArray.length; i++) {
			boolean contained = false;
			for (int j = freeRectArray.length - 1; j >= i; j--) {
				if (j != i && freeRectArray[j].contains(freeRectArray[i])) {
					contained = true;
					break;
				}
			}
			if (!contained)
				freeRectangles.add(freeRectArray[i]);
		}
	}


	/**
	 * Provides an area based comparison between two rectangles.
	 */
	private static final Comparator<Rectangle2D> RECTANGLE_AREA_COMPARATOR = new RectangleAreaComparator();

	/**
	 * Provides an area based comparison between two rectangles.
	 */
	private static class RectangleAreaComparator implements Comparator<Rectangle2D> {

		@Override
		public int compare(Rectangle2D arg0, Rectangle2D arg1) {
			double area0 = arg0.getWidth() * arg0.getHeight();
			double area1 = arg1.getWidth() * arg1.getHeight();
			return Double.compare(area0, area1);
		}

	}
}
