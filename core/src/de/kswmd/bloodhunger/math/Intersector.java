package de.kswmd.bloodhunger.math;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public final class Intersector {

    /**
     * returns the nearest point to the start point where it intersects the polygon
     *
     * @param start        the start point
     * @param end          the end point
     * @param poly         the polygon to check intersection
     * @param intersection the intersection Vector (nearest point to start)
     * @return true if intersects and false if it doesn't
     */
    public static boolean intersectSegmentPolygon(Vector2 start, Vector2 end, Polygon poly, Vector2 intersection) {
        float[] verts = poly.getTransformedVertices();
        float x1, x2;
        float y1, y2;
        x1 = verts[0];
        y1 = verts[1];
        x2 = verts[verts.length - 2];
        y2 = verts[verts.length - 1];

        float finalX = 0;
        float finalY = 0;
        boolean firstIntersect = true;

        for (int i = 0; i < verts.length; i += 2) {
            if (i % 4 == 0) {
                x1 = verts[i];
                y1 = verts[i + 1];
            } else {
                x2 = verts[i];
                y2 = verts[i + 1];
            }
            if (com.badlogic.gdx.math.Intersector.intersectSegments(x1, y1, x2, y2,
                    start.x, start.y, end.x, end.y, intersection)) {
                if (!firstIntersect) {
                    if (start.dst2(intersection) < start.dst2(finalX, finalY)) {
                        finalX = intersection.x;
                        finalY = intersection.y;
                    }
                } else {
                    finalX = intersection.x;
                    finalY = intersection.y;
                    firstIntersect = false;
                }
            }
        }
        intersection.set(finalX, finalY);
        return !firstIntersect;
    }

    public static boolean overlapConvexPolygons(Polygon polygonA, Polygon polygonB, MinimumTranslationVector mtv) {
        return overlapConvexPolygons(polygonA.getTransformedVertices(), polygonB.getTransformedVertices(), mtv);
    }

    public static boolean overlapConvexPolygons(float[] verticesA, float[] verticesB, MinimumTranslationVector mtv) {
        return overlapConvexPolygons(verticesA, 0, verticesA.length, verticesB, 0, verticesB.length, mtv);
    }

    public static boolean overlapConvexPolygons(float[] shapeA, int offsetA, int countA, float[] shapeB, int offsetB, int countB, MinimumTranslationVector mtv) {
        boolean overlaps = true;
        if (mtv != null) {
            mtv.depth = Float.MAX_VALUE;
            mtv.normal.setZero();
        }
        overlaps = overlapsOnAxisOfShape(shapeB, offsetB, countB, shapeA, offsetA, countA, mtv, true);
        if (overlaps) {
            overlaps = overlapsOnAxisOfShape(shapeA, offsetA, countA, shapeB, offsetB, countB, mtv, false);
        }

        if (!overlaps) {
            if (mtv != null) {
                mtv.depth = 0;
                mtv.normal.setZero();
            }
            return false;
        }
        return overlaps;
    }

    /**
     * @param shapeA        the shapeA
     * @param offsetA       offset of shapeA
     * @param countA        count of shapeA
     * @param shapeB        the shapeB
     * @param offsetB       offset of shapeB
     * @param countB        count of shapeB
     * @param mtv           the minimum translation vector
     * @param shapesShifted states if shape a and b are shifted. Important for calculating the axis translation for shapeA.
     * @return
     */
    private static boolean overlapsOnAxisOfShape(float[] shapeA, int offsetA, int countA, float[] shapeB, int offsetB, int countB, MinimumTranslationVector mtv, boolean shapesShifted) {
        int endA = offsetA + countA;
        int endB = offsetB + countB;
        //get axis of polygon A
        for (int i = offsetA; i < endA; i += 2) {
            float x1 = shapeA[i];
            float y1 = shapeA[i + 1];
            float x2 = shapeA[(i + 2) % countA];
            float y2 = shapeA[(i + 3) % countA];

            //Get the Axis for the 2 vertices
            float axisX = y1 - y2;
            float axisY = -(x1 - x2);

            float len = (float) Math.sqrt(axisX * axisX + axisY * axisY);
            //We got a normalized Vector
            axisX /= len;
            axisY /= len;
            float minA = Float.MAX_VALUE;
            float maxA = -Float.MAX_VALUE;
            //project shape a on axis
            for (int v = offsetA; v < endA; v += 2) {
                float p = shapeA[v] * axisX + shapeA[v + 1] * axisY;
                minA = Math.min(minA, p);
                maxA = Math.max(maxA, p);
            }

            float minB = Float.MAX_VALUE;
            float maxB = -Float.MAX_VALUE;

            //project shape b on axis
            for (int v = offsetB; v < endB; v += 2) {
                float p = shapeB[v] * axisX + shapeB[v + 1] * axisY;
                minB = Math.min(minB, p);
                maxB = Math.max(maxB, p);
            }
            //There is a gap
            if (maxA < minB || maxB < minA) {
                return false;
            } else {
                if (mtv != null) {
                    float o = Math.min(maxA, maxB) - Math.max(minA, minB);
                    boolean aContainsB = minA < minB && maxA > maxB;
                    boolean bContainsA = minB < minA && maxB > maxA;
                    //if it contains one or another
                    float mins = 0;
                    float maxs = 0;
                    if (aContainsB || bContainsA) {
                        mins = Math.abs(minA - minB);
                        maxs = Math.abs(maxA - maxB);
                        o += Math.min(mins, maxs);
                    }

                    if (mtv.depth > o) {
                        mtv.depth = o;
                        boolean condition;
                        if (shapesShifted) {
                            condition = minA < minB;
                            axisX = condition ? axisX : -axisX;
                            axisY = condition ? axisY : -axisY;
                        } else {
                            condition = minA > minB;
                            axisX = condition ? axisX : -axisX;
                            axisY = condition ? axisY : -axisY;
                        }

                        if(aContainsB || bContainsA){
                            condition = mins > maxs;
                            axisX = condition ? axisX : -axisX;
                            axisY = condition ? axisY : -axisY;
                        }

                        mtv.normal.set(axisX, axisY);
                    }
                }
            }
        }
        return true;
    }

    public static class MinimumTranslationVector {

        public final Vector2 normal = new Vector2();
        public float depth;
    }


}
