package de.kswmd.bloodhunger.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.kswmd.bloodhunger.BloodHungerGame;

public class LoadingScreen extends BaseScreen{

    public LoadingScreen(BloodHungerGame game) {
        super(game);
    }

    @Override
    protected void initialize() {
        game.assetManager.load("bloodHunger.atlas", TextureAtlas.class);
    }

    @Override
    protected void update(float delta) {
        AssetManager manager = game.assetManager;

        if(manager.update()){
            game.setScreen(new GameScreen(game));
        }
        float progress = manager.getProgress();
        Gdx.app.debug("Progress", progress + "%");
    }
}
