package de.kswmd.bloodhunger.math;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public final class Intersector {

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
                x2 = verts[i];
                y2 = verts[i + 1];
            } else {
                x1 = verts[i];
                y1 = verts[i + 1];
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
        intersection.set(finalX,finalY);
        return !firstIntersect;
    }

}
