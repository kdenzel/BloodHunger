package de.kswmd.bloodhunger.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ShortArray;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.factories.Box2DBodyFactory;
import de.kswmd.bloodhunger.listeners.BoundsCollisionListener;
import de.kswmd.bloodhunger.math.Intersector;
import de.kswmd.bloodhunger.utils.Mapper;

/**
 * for collision detection
 */
public class BoundsComponent implements Component, Disposable {

    private static final EarClippingTriangulator earClippingTriangulator = new EarClippingTriangulator();

    private final float width;
    private final float height;
    private final short category;
    //The attached entity to this component
    public final Entity entity;
    //Minimum translation vector for triangles
    private final Intersector.MinimumTranslationVector minimumTranslationVector = new Intersector.MinimumTranslationVector();

    private final Array<BoundsCollisionListener> listeners = new Array<>();
    //The arrays for collision detection
    private final Array<Polygon> boundaryPolygonArray = new Array<>(1);
    private final Array<Array<Polygon>> trianglesArray = new Array<>(1);
    private final Array<Body> box2DBodyArray = new Array<>(1);
    //A pool for all polygon objects on the layer
    private final Array<Array<Polygon>> trianglesPoolArray = new Array<>(1);

    public BoundsComponent(float width, float height, short category, Entity entity) {
        this.width = width;
        this.height = height;
        this.category = category;
        this.entity = entity;
    }

    public void rotate(float degree) {
        boundaryPolygonArray.forEach(polygon -> {
            if (polygon == null)
                return;
            polygon.setOrigin(width / 2, height / 2);
            polygon.setRotation(degree);
        });
        trianglesArray.forEach(triangles -> {
            if (triangles == null)
                return;
            triangles.forEach(triangle -> {
                triangle.setOrigin(width / 2, height / 2);
                triangle.setRotation(degree);
            });
        });
        box2DBodyArray.forEach(body -> {
                    if (body == null)
                        return;
                    body.setTransform(0, 0, degree * MathUtils.degreesToRadians);
                }
        );
    }

    public BoundsComponent setBoundaryRectangle(int z) {
        float w = width;
        float h = height;
        float[] vertices = {0, 0, w, 0, w, h, 0, h};
        setPolygon(vertices, z);
        return this;
    }

    public BoundsComponent setBoundaryPolygon(int numSides, int z) {
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
        return this;
    }

    public BoundsComponent setPolygon(float[] vertices, int z) {
        Polygon p = null;
        if (boundaryPolygonArray.size > z) {
            p = boundaryPolygonArray.get(z);
        }
        if (p == null) {
            if (GeometryUtils.isClockwise(vertices, 0, vertices.length))
                throw new RuntimeException("FORM IS CLOCKWISE!!!!!!!");
            p = new Polygon(vertices);
            Rectangle r = p.getBoundingRectangle();
            while (z > boundaryPolygonArray.size) {
                boundaryPolygonArray.add(null);
                box2DBodyArray.add(null);
                trianglesArray.add(null);
                trianglesPoolArray.add(null);
            }
            boundaryPolygonArray.insert(z, p);

            ShortArray indices = earClippingTriangulator.computeTriangles(vertices);
            Array<Polygon> triangles = new Array<>(indices.size);
            Array<Polygon> trianglesPool = new Array<>(indices.size);
            for (int index = 0; index < indices.size; index += 3) {
                int p1 = indices.get(index + 2) * 2;
                int p2 = indices.get(index + 1) * 2;
                int p3 = indices.get(index) * 2;

                float[] tmpVerts = {
                        vertices[p1], vertices[p1 + 1],
                        vertices[p2], vertices[p2 + 1],
                        vertices[p3], vertices[p3 + 1]
                };
                Polygon poly = new Polygon(tmpVerts);
                triangles.add(poly);
                trianglesPool.add(poly);
                if (GeometryUtils.isClockwise(poly.getVertices(), 0, poly.getVertices().length))
                    throw new RuntimeException("TRIANGLE IS CLOCKWISE!!!!!!!");
            }
            this.trianglesArray.add(triangles);
            this.trianglesPoolArray.add(trianglesPool);
            box2DBodyArray.insert(z, Box2DBodyFactory.createKinematicRectanglePolygonBody(width, height, category));
            rotate(0);
        } else {
            if (GeometryUtils.isClockwise(vertices, 0, vertices.length))
                throw new RuntimeException("FORM IS CLOCKWISE!!!!!!!");
            //IMPORTANT: To get the correct bounding rectangle, we have to rotate to 0 degrees (start position) first
            rotate(0);
            p = boundaryPolygonArray.get(z);
            Array<Polygon> triangles = trianglesArray.get(z);
            Array<Polygon> trianglesPool = this.trianglesPoolArray.get(z);
            triangles.clear();
            ShortArray indices = earClippingTriangulator.computeTriangles(vertices);
            int i = 0;
            for (int index = 0; index < indices.size; index += 3) {
                int p1 = indices.get(index + 2) * 2;
                int p2 = indices.get(index + 1) * 2;
                int p3 = indices.get(index) * 2;

                float[] tmpVerts = {
                        vertices[p1], vertices[p1 + 1],
                        vertices[p2], vertices[p2 + 1],
                        vertices[p3], vertices[p3 + 1]
                };
                Polygon poly;
                if (i >= trianglesPool.size) {
                    trianglesPool.add(new Polygon());
                }
                poly = trianglesPool.get(i);
                poly.setVertices(tmpVerts);
                triangles.add(poly);
                i++;
                if (GeometryUtils.isClockwise(poly.getVertices(), 0, poly.getVertices().length))
                    throw new RuntimeException("TRIANGLE IS CLOCKWISE!!!!!!!");
            }
            p.setVertices(vertices);
            Rectangle r = p.getBoundingRectangle();
            float hx = r.width / 2;
            float hy = r.height / 2;
            Body b = box2DBodyArray.get(z);
            Array<Fixture> fixtures = b.getFixtureList();
            fixtures.forEach(fixture -> {
                switch (fixture.getType()) {
                    case Polygon:
                        PolygonShape ps = (PolygonShape) fixture.getShape();
                        ps.setAsBox(hx, hy);
                        break;
                }
            });
        }
        return this;
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
     *
     * @param x - x-coordinate
     * @param y - y-coordinate
     */
    public BoundsComponent setPosition(float x, float y) {
        for (int z = 0; z < boundaryPolygonArray.size; z++) {
            setPosition(x, y, z);
        }
        return this;
    }

    private BoundsComponent setPosition(float x, float y, int z) {
        if (getPolygon(z) == null)
            return this;
        Polygon boundaryPolygon = boundaryPolygonArray.get(z);
        boundaryPolygon.setPosition(x, y);
        trianglesArray.get(z).forEach(triangle -> triangle.setPosition(x, y));
        Body b = box2DBodyArray.get(z);
        b.setTransform(x + width / 2, y + height / 2, b.getAngle());
        return this;
    }

    public Array<Polygon> getTriangles(int z) {
        return trianglesArray.get(z);
    }

    public void addListener(BoundsCollisionListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(BoundsCollisionListener listener) {
        this.listeners.removeValue(listener, true);
    }

    private void notifyOnTriangleCollide(BoundsComponent other, Polygon triangle, Polygon otherTriangle, Intersector.MinimumTranslationVector minimumTranslationVector, int zlayer, int triangleCount) {
        listeners.forEach(l -> l.onTriangleCollide(this, other, triangle, otherTriangle, minimumTranslationVector, zlayer, triangleCount));
    }

    /**
     * checks if this boundscomponent intersects with another boundscomponent on a specific layer
     * BE AWARE THE POLYGONS MUST BE COUNTER CLOCKWISE OTHERWISE GLITCHES APPEAR!!!!!!!1111!!!!!!!!!!!!!!!!
     *
     * @param other the other boundscomponent
     * @param z     the z-layer
     * @return if the triangles intercept each others
     */
    public boolean intersects(BoundsComponent other, int z) {
        if (this == other)
            return false;

        if (other.size() <= z || size() <= z)
            return false;
        Polygon otherPolygon = other.getPolygon(z);
        Polygon thisPolygon = this.getPolygon(z);
        if (otherPolygon == null || thisPolygon == null)
            return false;

        if (!thisPolygon.getBoundingRectangle().overlaps(otherPolygon.getBoundingRectangle()))
            return false;

        //Works if boundscollision system is at the current position as system in the engine.
        /*if(Intersector.overlapConvexPolygons(thisPolygon,otherPolygon,minimumTranslationVector)) {
            notifyOnTriangleCollide(other, thisPolygon, otherPolygon, minimumTranslationVector, z, 0);
            return true;
        }*/

        boolean intersect = false;
        //There is a bug in the libgdx Intersector class, it returns wrong minimumTranslationVector values so use your own for overlapConvexPolygons function.
        Array<Polygon> thisTriangles = this.trianglesArray.get(z);
        Array<Polygon> otherTriangles = other.trianglesArray.get(z);
        int intersectCount = 0;
        if (Mapper.playerComponent.has(entity)) {
            Gdx.app.debug("INTERSECT START", "##################");
        }
        for (int i = 0; i < thisTriangles.size; i++) {
            Polygon triangle = thisTriangles.get(i);
            for (int j = 0; j < otherTriangles.size; j++) {
                Polygon otherTriangle = otherTriangles.get(j);
                    if (triangle.getBoundingRectangle().overlaps(otherTriangle.getBoundingRectangle())) {
                        if (Intersector.overlapConvexPolygons(triangle, otherTriangle, minimumTranslationVector)) {
                            notifyOnTriangleCollide(other, triangle, otherTriangle, minimumTranslationVector, z, intersectCount);
                            intersect = true;
                            intersectCount++;
                        }
                    }
            }
        }
        if (Mapper.playerComponent.has(entity)) {
            Gdx.app.debug("INTERSECT END", "##################");
        }
        return intersect;
    }

    @Override
    public void dispose() {
        listeners.clear();
        boundaryPolygonArray.clear();
        trianglesArray.clear();
        trianglesPoolArray.clear();
        box2DBodyArray.forEach(body -> {
            if (body == null)
                return;
            BloodHungerGame.WORLD.destroyBody(body);
        });
    }
}
