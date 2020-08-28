package de.kswmd.bloodhunger.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
import de.kswmd.bloodhunger.Assets;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.components.LevelExitComponent;
import de.kswmd.bloodhunger.factories.EntityFactory;
import de.kswmd.bloodhunger.utils.LevelManager;

import java.util.List;

public class LoadNextLevelScreen extends BaseScreen {

    private ShapeRenderer shapeRenderer;
    private Screen nextScreen;

    public LoadNextLevelScreen(BloodHungerGame game) {
        super(game);
    }

    @Override
    protected void initialize() {
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.setColor(Color.WHITE);
        BloodHungerGame.ASSET_MANAGER.load(LevelManager.getInstance().level.getMap(), TiledMap.class);
    }

    public void setNextScreen(Screen nextScreen) {
        this.nextScreen = nextScreen;
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
            LevelManager levelManager = LevelManager.getInstance();
            levelManager.setTiledMap(BloodHungerGame.ASSET_MANAGER.get(LevelManager.getInstance().getMap()));
            Array<Entity> entities = levelManager.loadMapObjects(game);
            entities.forEach(entity -> game.engine.addEntity(entity));
            //update maprenderer
            game.renderingSystem.updateLevel();
            game.setScreen(nextScreen);
        }
    }
}
