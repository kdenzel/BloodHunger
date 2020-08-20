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

public class LoadingScreen extends BaseScreen {

    private ShapeRenderer shapeRenderer;

    public LoadingScreen(BloodHungerGame game) {
        super(game);
    }

    @Override
    protected void initialize() {
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.setColor(Color.WHITE);
        BloodHungerGame.ASSET_MANAGER.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        BloodHungerGame.ASSET_MANAGER.load(LevelManager.getInstance().level.getMap(), TiledMap.class);
        BloodHungerGame.ASSET_MANAGER.load(Assets.TEXTURE_ATLAS_ANIMATIONS, TextureAtlas.class);
        BloodHungerGame.ASSET_MANAGER.load(Assets.TEXTURE_ATLAS_PARTICLES, TextureAtlas.class);
        BloodHungerGame.ASSET_MANAGER.load(Assets.TEXTURE_ATLAS_IMAGES, TextureAtlas.class);
    }

    @Override
    protected void update(float delta) {
        shapeRenderer.begin();
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        AssetManager manager = BloodHungerGame.ASSET_MANAGER;
        float progress = manager.getProgress();
        int bulkHeight = 40;

        shapeRenderer.rect(0, Gdx.graphics.getHeight() / 2f - bulkHeight / 2f, Gdx.graphics.getWidth() * progress, bulkHeight);
        Gdx.app.debug("Progress", progress + "%");
        shapeRenderer.end();
        if (manager.update()) {
            if (BloodHungerGame.SCREEN_GAME == null) {
                BloodHungerGame.SCREEN_GAME = new GameScreen(game);
            }
            game.setScreen(BloodHungerGame.SCREEN_GAME);
        }
    }
}
