package de.kswmd.bloodhunger.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;

/**
 * for collision detection
 */
public class BoundsComponent implements Component {

    private float width;
    private float height;
    public Polygon boundaryPolygon;

    public BoundsComponent(DimensionComponent dimensionComponent) {
        this.width = dimensionComponent.width;
        this.height = dimensionComponent.height;
        setBoundaryRectangle();
    }

    public BoundsComponent(DimensionComponent dimensionComponent, int numSides) {
        this.width = dimensionComponent.width;
        this.height = dimensionComponent.height;
        setBoundaryPolygon(numSides);
    }

    public BoundsComponent(DimensionComponent dimensionComponent, float scale) {
        this.width = dimensionComponent.width * scale;
        this.height = dimensionComponent.height * scale;
        setBoundaryRectangle();
    }

    public BoundsComponent(DimensionComponent dimensionComponent, float scale, int numSides) {
        this.width = dimensionComponent.width * scale;
        this.height = dimensionComponent.height * scale;
        setBoundaryPolygon(numSides);
    }

    public BoundsComponent(DimensionComponent dimensionComponent, float scaleX, float scaleY) {
        this.width = dimensionComponent.width * scaleX;
        this.height = dimensionComponent.height * scaleY;
        setBoundaryRectangle();
    }

    public BoundsComponent(DimensionComponent dimensionComponent, float scaleX, float scaleY, int numSides) {
        this.width = dimensionComponent.width * scaleX;
        this.height = dimensionComponent.height * scaleY;
        setBoundaryPolygon(numSides);
    }

    public BoundsComponent(float width, float height) {
        this.width = width;
        this.height = height;
        setBoundaryRectangle();
    }

    public BoundsComponent(float width, float height, int numSides) {
        this.width = width;
        this.height = height;
        setBoundaryPolygon(numSides);
    }

    public BoundsComponent(float width, float height, float[] vertices) {
        this.width = width;
        this.height = height;
        setPolygon(vertices);
    }

    public void rotate(float degree) {
        boundaryPolygon.setOrigin(width / 2, height / 2);
        boundaryPolygon.setRotation(degree);
    }

    private void setBoundaryRectangle() {
        float w = width;
        float h = height;
        float[] vertices = {0, 0, w, 0, w, h, 0, h};
        if (boundaryPolygon == null)
            boundaryPolygon = new Polygon(vertices);
        else
            boundaryPolygon.setVertices(vertices);
        rotate(0);
    }

    public void setBoundaryPolygon(int numSides) {
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
        if (boundaryPolygon == null)
            boundaryPolygon = new Polygon(vertices);
        else
            boundaryPolygon.setVertices(vertices);
        rotate(0);
    }

    public void setPolygon(float[] vertices) {
        if (boundaryPolygon == null)
            boundaryPolygon = new Polygon(vertices);
        else
            boundaryPolygon.setVertices(vertices);
        rotate(0);
    }

}
