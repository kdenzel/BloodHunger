package de.kswmd.bloodhunger.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.Assets;
import de.kswmd.bloodhunger.utils.LevelManager;

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
        game.assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        game.assetManager.load(Assets.BLOODHUNGER_TEXTURE_ATLAS, TextureAtlas.class);
        game.assetManager.load(LevelManager.getInstance().level.getMap(), TiledMap.class);
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
