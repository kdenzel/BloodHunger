package de.kswmd.bloodhunger.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.factories.Box2DBodyFactory;

/**
 * for collision detection
 */
public class BoundsComponent implements Component, Disposable {

    private final float width;
    private final float height;
    private final short category;
    private final Array<Polygon> boundaryPolygonArray = new Array<>(1);
    private final Array<Body> box2DBodyArray = new Array<>(1);

    public BoundsComponent(DimensionComponent dimensionComponent, short category) {
        this.width = dimensionComponent.width;
        this.height = dimensionComponent.height;
        this.category = category;
        setBoundaryRectangle(0);
    }

    public BoundsComponent(DimensionComponent dimensionComponent, int numSides, short category) {
        this.width = dimensionComponent.width;
        this.height = dimensionComponent.height;
        this.category = category;
        setBoundaryPolygon(numSides, 0);
    }

    public BoundsComponent(DimensionComponent dimensionComponent, float scale,short category) {
        this.width = dimensionComponent.width * scale;
        this.height = dimensionComponent.height * scale;
        this.category = category;
        setBoundaryRectangle(0);
    }

    public BoundsComponent(DimensionComponent dimensionComponent, float scale, int numSides, short category) {
        this.width = dimensionComponent.width * scale;
        this.height = dimensionComponent.height * scale;
        this.category = category;
        setBoundaryPolygon(numSides, 0);
    }

    public BoundsComponent(DimensionComponent dimensionComponent, float scaleX, float scaleY, short category) {
        this.width = dimensionComponent.width * scaleX;
        this.height = dimensionComponent.height * scaleY;
        this.category = category;
        setBoundaryRectangle(0);
    }

    public BoundsComponent(DimensionComponent dimensionComponent, float scaleX, float scaleY, int numSides, short category) {
        this.width = dimensionComponent.width * scaleX;
        this.height = dimensionComponent.height * scaleY;
        this.category = category;
        setBoundaryPolygon(numSides, 0);
    }

    public BoundsComponent(float width, float height, short category) {
        this.width = width;
        this.height = height;
        this.category = category;
        setBoundaryRectangle(0);
    }

    public BoundsComponent(float width, float height, int numSides, short category) {
        this.width = width;
        this.height = height;
        this.category = category;
        setBoundaryPolygon(numSides, 0);
    }

    public BoundsComponent(float width, float height, float[] vertices, short category) {
        this.width = width;
        this.height = height;
        this.category = category;
        setPolygon(vertices, 0);
    }

    public void rotate(float degree) {
        boundaryPolygonArray.forEach(polygon -> {
            polygon.setOrigin(width / 2, height / 2);
            polygon.setRotation(degree);
        });
        box2DBodyArray.forEach(body ->
                body.setTransform(0,0, degree * MathUtils.degreesToRadians)
        );
    }

    private void setBoundaryRectangle(int z) {
        float w = width;
        float h = height;
        float[] vertices = {0, 0, w, 0, w, h, 0, h};
        setPolygon(vertices, z);

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
        setPolygon(vertices, z);
    }

    public void setPolygon(float[] vertices, int z) {
        Polygon p;
        if (boundaryPolygonArray.size <= z) {
            p = new Polygon(vertices);
            Rectangle r = p.getBoundingRectangle();
            boundaryPolygonArray.insert(z, p);
            box2DBodyArray.insert(z, Box2DBodyFactory.createKinematicRectanglePolygonBody(width,height, category));
            rotate(0);
        } else {
            //IMPORTANT: To get the correct bounding rectangle, we have to rotate to 0 degrees (start position) first
            rotate(0);
            p = boundaryPolygonArray.get(z);
            p.setVertices(vertices);
            Rectangle r = p.getBoundingRectangle();
            float hx = r.width/2;
            float hy = r.height/2;
            Body b = box2DBodyArray.get(z);
            Array<Fixture> fixtures = b.getFixtureList();
            fixtures.forEach(fixture -> {
                switch (fixture.getType()) {
                    case Polygon:
                        PolygonShape ps = (PolygonShape) fixture.getShape();
                        ps.setAsBox(hx,hy);
                        break;

                }
            });
        }
    }

    public Polygon getPolygon(int z) {
        return boundaryPolygonArray.get(z);
    }

    public Body getBody(int z) {
        return box2DBodyArray.get(z);
    }

    public Polygon removePolygon(int z) {
        return boundaryPolygonArray.removeIndex(z);
    }

    public int size() {
        return boundaryPolygonArray.size;
    }

    /**
     * Move every polygon on all layers
     * @param x - x-coordinate
     * @param y - y-coordinate
     */
    public void setPosition(float x, float y){
        for(int z = 0; z < boundaryPolygonArray.size; z++){
            setPosition(x,y,z);
        }
    }

    private void setPosition(float x, float y, int z) {
        boundaryPolygonArray.get(z).setPosition(x, y);
        Body b = box2DBodyArray.get(z);
        b.setTransform(x+width/2, y+height/2, b.getAngle());
    }


    @Override
    public void dispose() {
        box2DBodyArray.forEach(BloodHungerGame.WORLD::destroyBody);
    }
}
