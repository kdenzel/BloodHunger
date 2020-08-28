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
import de.kswmd.bloodhunger.components.LevelExitComponent;
import de.kswmd.bloodhunger.utils.LevelManager;

public class InitialLoadingScreen extends BaseScreen {

    private ShapeRenderer shapeRenderer;

    public InitialLoadingScreen(BloodHungerGame game) {
        super(game);
    }

    @Override
    protected void initialize() {
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.setColor(Color.WHITE);
        BloodHungerGame.ASSET_MANAGER.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        BloodHungerGame.ASSET_MANAGER.load(Assets.TEXTURE_ATLAS_GAME_ANIMATIONS, TextureAtlas.class);
        BloodHungerGame.ASSET_MANAGER.load(Assets.TEXTURE_ATLAS_PARTICLES, TextureAtlas.class);
        BloodHungerGame.ASSET_MANAGER.load(Assets.TEXTURE_ATLAS_IMAGES, TextureAtlas.class);
        BloodHungerGame.ASSET_MANAGER.load(Assets.TEXTURE_ATLAS_UI, TextureAtlas.class);
        BloodHungerGame.ASSET_MANAGER.load(Assets.TEXTURE_ATLAS_SCENES, TextureAtlas.class);
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
            game.initAfterLoading();
            game.setLevel(new LevelExitComponent(BloodHungerGame.SCREEN_GAME, LevelManager.Level.LEVEL_1));
            this.dispose();
        }
    }
}
