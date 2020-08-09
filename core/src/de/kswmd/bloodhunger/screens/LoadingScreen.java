package de.kswmd.bloodhunger.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.kswmd.bloodhunger.BloodHungerGame;

public class LoadingScreen extends BaseScreen{

    private ShapeRenderer shapeRenderer;

    public LoadingScreen(BloodHungerGame game) {
        super(game);
    }

    @Override
    protected void initialize() {
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.setColor(Color.WHITE);
        game.assetManager.load("bloodHunger.atlas", TextureAtlas.class);
    }

    @Override
    protected void update(float delta) {
        shapeRenderer.begin();
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        AssetManager manager = game.assetManager;
        float progress = manager.getProgress();
        int bulkHeight = 40;

        shapeRenderer.rect(0,Gdx.graphics.getHeight()/2-bulkHeight/2,Gdx.graphics.getWidth()*progress,bulkHeight);
        Gdx.app.debug("Progress", progress + "%");
        shapeRenderer.end();
        if(manager.update()){
            game.setScreen(new GameScreen(game));
        }
    }
}
