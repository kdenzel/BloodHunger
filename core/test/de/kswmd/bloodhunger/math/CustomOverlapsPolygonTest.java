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
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector3;
import de.kswmd.bloodhunger.BaseTest;
import de.kswmd.bloodhunger.ListTestAdapter;
import org.junit.jupiter.api.Test;

public class CustomOverlapsPolygonTest extends BaseTest {

    private static final String TAG = CustomOverlapsPolygonTest.class.getSimpleName();
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;

    private float triangleWidth = 1f;
    private float triangleHeight = 1f;
    //2 triangle polygons intersect at 0,10 - 10,10 will return the wrong direction
    private float[] vertsTriangle1 = {0f, 0f, triangleWidth, triangleHeight, triangleWidth, 0};
    private float[] vertsTriangle2 = {0f, 0f, triangleWidth*2, 0, triangleWidth*2, triangleHeight*2};

    private Polygon triangle1 = new Polygon();
    private Polygon triangle2 = new Polygon();

    private Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();

    private Vector3 mouseCoords = new Vector3();

    @Test
    public void testIntersectorTrianglePolygon() {
        //set inital position
        triangle1.setVertices(vertsTriangle1);
        triangle2.setVertices(vertsTriangle2);
        triangle1.setPosition(0, 0);
        triangle2.setPosition(1, 0);
    }

    @Override
    public void show() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        camera.position.set(0, 0, 0);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        testIntersectorTrianglePolygon();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = 8;
        camera.viewportHeight = 6;
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
            Gdx.app.debug(TAG, mtv.normal + " " + mtv.depth + " overlaps: " + overlaps + " " + mouseCoords);
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
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.line(0, 0, mtv.normal.x * 10, mtv.normal.y * 10);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.line(0, 0, mtv.normal.x * -10, mtv.normal.y * -10);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.line(0, 0, mtv.normal.x * mtv.depth, mtv.normal.y * mtv.depth);
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
        ListTestAdapter adapter = new ListTestAdapter(new CustomOverlapsPolygonTest());
        new LwjglApplication(adapter, config);
    }
}
