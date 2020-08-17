package de.kswmd.bloodhunger.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Array;

/**
 * for collision detection
 */
public class BoundsComponent implements Component {

    private float width;
    private float height;
    private final Array<Polygon> boundaryPolygonArray = new Array<>();

    public BoundsComponent(DimensionComponent dimensionComponent) {
        this.width = dimensionComponent.width;
        this.height = dimensionComponent.height;
        setBoundaryRectangle(0);
    }

    public BoundsComponent(DimensionComponent dimensionComponent, int numSides) {
        this.width = dimensionComponent.width;
        this.height = dimensionComponent.height;
        setBoundaryPolygon(numSides, 0);
    }

    public BoundsComponent(DimensionComponent dimensionComponent, float scale) {
        this.width = dimensionComponent.width * scale;
        this.height = dimensionComponent.height * scale;
        setBoundaryRectangle(0);
    }

    public BoundsComponent(DimensionComponent dimensionComponent, float scale, int numSides) {
        this.width = dimensionComponent.width * scale;
        this.height = dimensionComponent.height * scale;
        setBoundaryPolygon(numSides, 0);
    }

    public BoundsComponent(DimensionComponent dimensionComponent, float scaleX, float scaleY) {
        this.width = dimensionComponent.width * scaleX;
        this.height = dimensionComponent.height * scaleY;
        setBoundaryRectangle(0);
    }

    public BoundsComponent(DimensionComponent dimensionComponent, float scaleX, float scaleY, int numSides) {
        this.width = dimensionComponent.width * scaleX;
        this.height = dimensionComponent.height * scaleY;
        setBoundaryPolygon(numSides, 0);
    }

    public BoundsComponent(float width, float height) {
        this.width = width;
        this.height = height;
        setBoundaryRectangle(0);
    }

    public BoundsComponent(float width, float height, int numSides) {
        this.width = width;
        this.height = height;
        setBoundaryPolygon(numSides, 0);
    }

    public BoundsComponent(float width, float height, float[] vertices) {
        this.width = width;
        this.height = height;
        setPolygon(vertices, 0);
    }

    public void rotate(float degree) {
        boundaryPolygonArray.forEach(polygon -> {
            polygon.setOrigin(width / 2, height / 2);
            polygon.setRotation(degree);
        });
    }

    private void setBoundaryRectangle(int z) {
        float w = width;
        float h = height;
        float[] vertices = {0, 0, w, 0, w, h, 0, h};
        if (boundaryPolygonArray.size >= z)
            boundaryPolygonArray.insert(z, new Polygon(vertices));
        else
            boundaryPolygonArray.get(z).setVertices(vertices);
        rotate(0);
    }

    public void setBoundaryPolygon(int numSides, int z) {
        float w = width;
        float h = height;
        float[] vertices = new float[2 * numSides];
        for (int i = 0; i < numSides; i++) {
            float angle = i * (MathUtils.PI * 2) / numSides;
            // x-coordinate
            vertices[2 * i] = w / 2 * MathUtils.cos(angle) + w / 2;
            // y-coordinate
            vertices[2 * i + 1] = h / 2 * MathUtils.sin(angle) + h / 2;
        }
        if (boundaryPolygonArray.size >= z)
            boundaryPolygonArray.insert(z, new Polygon(vertices));
        else
            boundaryPolygonArray.get(z).setVertices(vertices);
        rotate(0);
    }

    public void setPolygon(float[] vertices, int z) {
        if (boundaryPolygonArray.size == z)
            boundaryPolygonArray.insert(z, new Polygon(vertices));
        else
            boundaryPolygonArray.get(z).setVertices(vertices);
        rotate(0);
    }

    public Polygon getPolygon(int z) {
        return boundaryPolygonArray.get(z);
    }

    public Polygon removePolygon(int z){
        return boundaryPolygonArray.removeIndex(z);
    }

    public int size() {
        return boundaryPolygonArray.size;
    }

}
