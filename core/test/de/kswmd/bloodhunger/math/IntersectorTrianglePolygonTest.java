package de.kswmd.bloodhunger.math;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import de.kswmd.bloodhunger.BaseTest;
import de.kswmd.bloodhunger.ListTestAdapter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IntersectorTrianglePolygonTest extends BaseTest {

    private static final String TAG = IntersectorTrianglePolygonTest.class.getSimpleName();
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;

    private float triangleWidth = 10f;
    private float triangleHeight = 10f;
    //2 triangle polygons intersect at 0,10 - 10,10 will return the wrong direction
    private float[] vertsTriangle1 = {0f, 0f, triangleWidth, 0f, triangleWidth, triangleHeight};
    private float[] vertsTriangle2 = {0f, 0f, triangleWidth, 0f, triangleWidth, triangleHeight};

    private Polygon triangle1 = new Polygon();
    private Polygon triangle2 = new Polygon();

    private Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();

    private Vector3 mouseCoords = new Vector3();

    @Test
    public void testIntersectorTrianglePolygon() {
        triangle1.setVertices(vertsTriangle1);
        triangle2.setVertices(vertsTriangle2);
        triangle1.setPosition(0, 0);
        triangle2.setPosition(10, 0);

        assertTrue(Intersector.overlapConvexPolygons(triangle1, triangle2, mtv));
        assertEquals(0f, mtv.depth, 0.0001f);
        assertEquals(new Vector2(-1, -0f), mtv.normal);

        triangle2.setPosition(-10, 10);
        assertFalse(Intersector.overlapConvexPolygons(triangle1, triangle2, mtv));
        triangle2.setOrigin(triangleWidth, 0);
        triangle2.rotate(180);
        triangle2.getTransformedVertices();
        assertTrue(Intersector.overlapConvexPolygons(triangle1, triangle2, mtv));
        triangle1.setPosition(-triangleWidth, triangle1.getY());
        assertTrue(Intersector.overlapConvexPolygons(triangle1, triangle2, mtv));
    }

    @Override
    public void show() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        camera = new OrthographicCamera();
        camera.setToOrtho(true);
        camera.position.set(0, 0, 0);
        shapeRenderer = new ShapeRenderer();
        testIntersectorTrianglePolygon();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = 80;
        camera.viewportHeight = 60;
    }

    private void update(float deltaTime) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            Intersector.overlapConvexPolygons(triangle1, triangle2, mtv);
            float x = triangle1.getX() + (mtv.normal.x * mtv.depth);
            float y = triangle1.getY() + (mtv.normal.y * mtv.depth);
            triangle1.setPosition(x, y);
            Gdx.app.debug(TAG, mtv.normal + " " + mtv.depth);
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            mouseCoords.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(mouseCoords);
            triangle1.setPosition(mouseCoords.x, mouseCoords.y);
            boolean overlaps = Intersector.overlapConvexPolygons(triangle1, triangle2, mtv);
            Gdx.app.debug(TAG, mtv.normal + " " + mtv.depth + " overlaps: " + overlaps);
        } else if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            triangle1.rotate(90);
            boolean overlaps = Intersector.overlapConvexPolygons(triangle1, triangle2, mtv);
            Gdx.app.debug(TAG, mtv.normal + " " + mtv.depth + " overlaps: " + overlaps);
        }
    }

    @Override
    public void render(float deltaTime) {
        update(deltaTime);
        camera.update();
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.polygon(triangle1.getTransformedVertices());
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.polygon(triangle2.getTransformedVertices());
        shapeRenderer.end();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide () {
        shapeRenderer.dispose();
    }

    @Override
    public void dispose() {

    }

    public static void main(String[] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.forceExit = false;
        config.width = 800;
        config.height = 600;
        config.fullscreen = false;
        ListTestAdapter listTestAdapter = new ListTestAdapter(new IntersectorTrianglePolygonTest());
        new LwjglApplication(listTestAdapter, config);
    }
}
