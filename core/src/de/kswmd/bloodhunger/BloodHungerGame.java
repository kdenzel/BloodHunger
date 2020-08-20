package de.kswmd.bloodhunger;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Logger;
import de.kswmd.bloodhunger.screens.LoadingScreen;

public class BloodHungerGame extends Game {

    public static Screen SCREEN_LOADING;
    public static Screen SCREEN_GAME;

    public static final int UNIT = 64;
    public static final float UNIT_SCALE = 1f / UNIT;

    public static final AssetManager ASSET_MANAGER = new AssetManager();
    public final boolean debug;

    public BloodHungerGame(boolean debug) {
        this.debug = debug;
    }

    @Override
    public void create() {
        if (debug) {
            Gdx.app.setLogLevel(Application.LOG_DEBUG);
            ASSET_MANAGER.getLogger().setLevel(Logger.DEBUG);
        }
        InputMultiplexer im = new InputMultiplexer();
        Gdx.input.setInputProcessor(im);
        SCREEN_LOADING = new LoadingScreen(this);
        setScreen(BloodHungerGame.SCREEN_LOADING);
    }

    @Override
    public void dispose() {
        super.dispose();
        ASSET_MANAGER.dispose();
        SCREEN_LOADING.dispose();
        SCREEN_GAME.dispose();
    }
}
