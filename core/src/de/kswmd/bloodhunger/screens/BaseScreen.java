package de.kswmd.bloodhunger.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import de.kswmd.bloodhunger.BloodHungerGame;

public abstract class BaseScreen extends ScreenAdapter implements InputProcessor {

    private static final String TAG = BaseScreen.class.getSimpleName();
    protected final BloodHungerGame game;
    protected Stage uiStage;

    public BaseScreen(BloodHungerGame game) {
        this.game = game;
        uiStage = new Stage(new StretchViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight()));
    }

    @Override
    public void show() {
        InputMultiplexer im = (InputMultiplexer) Gdx.input.getInputProcessor();
        //Order is important, if uiStage comes after, drag and drop does not work anymore.
        im.addProcessor(uiStage);
        im.addProcessor(this);
        initialize();
        Gdx.app.debug(TAG,"show Screen " + this);
    }

    @Override
    public void resize(int width, int height) {
        uiStage.getViewport().update(width,height,true);
        game.camera.update();
        Gdx.app.debug(TAG,"resize Screen " + this);
    }

    @Override
    public void render(float delta) {
        uiStage.act(delta);
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        update(delta);
        uiStage.draw();
    }

    @Override
    public void hide() {
        InputMultiplexer im = (InputMultiplexer)Gdx.input.getInputProcessor();
        im.removeProcessor(this);
        im.removeProcessor(uiStage);
        uiStage.clear();
        Gdx.app.debug(TAG,"hide Screen " + this);
    }

    @Override
    public void dispose() {
        Gdx.app.debug(TAG,"dispose Screen " + this);
        uiStage.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    protected abstract void initialize();

    protected abstract void update(float delta);
}
