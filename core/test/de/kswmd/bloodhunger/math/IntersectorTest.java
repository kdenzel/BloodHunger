package de.kswmd.bloodhunger.math;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class IntersectorTest {

    @Test
    public void testIntersectorSegmentPolygon() {
        //a rectangle polygon
        float[] verts = {5f,5f,10f,5f,10f,10f,5f,10f};
        Polygon poly = new Polygon(verts);
        Vector2 intersect = new Vector2();
        //Start line at bottom to top
        Vector2 start = new Vector2(7.5f,0f);
        Vector2 end = new Vector2(7.5f,20f);
        Intersector.intersectSegmentPolygon(start, end, poly, intersect);
        assertEquals(7.5f,intersect.x,0.001f);
        assertEquals(5f,intersect.y,0.001f);
        //check right side
        start.set(11,7.5f);
        end.set(0f,7.5f);
        Intersector.intersectSegmentPolygon(start, end, poly, intersect);
        assertEquals(10f,intersect.x,0.001f);
        assertEquals(7.5f,intersect.y,0.001f);
        //check top side
        start.set(7.5f,20f);
        end.set(7.5f,0f);
        Intersector.intersectSegmentPolygon(start, end, poly, intersect);
        assertEquals(7.5f,intersect.x,0.001f);
        assertEquals(10f,intersect.y,0.001f);
        //check left side
        start.set(0,7.5f);
        end.set(20f,7.5f);
        Intersector.intersectSegmentPolygon(start, end, poly, intersect);
        assertEquals(5,intersect.x,0.001f);
        assertEquals(7.5f,intersect.y,0.001f);
    }

}