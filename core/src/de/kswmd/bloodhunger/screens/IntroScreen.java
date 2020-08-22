package de.kswmd.bloodhunger.screens;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.factories.EntityFactory;
import de.kswmd.bloodhunger.utils.LevelManager;

public class IntroScreen extends BaseScreen {

    public IntroScreen(BloodHungerGame game) {
        super(game);
    }

    @Override
    protected void initialize() {
        uiStage.addActor(new Label("HAHA",game.uiSkin));
        game.setAmbientLight(0,0,0,1);
        game.engine.addEntity(EntityFactory.createPlayer(10,10,game.inventory));
        game.engine.addEntity(EntityFactory.createLevelExit(-0.5f,-0.5f,1,1,BloodHungerGame.SCREEN_GAME, LevelManager.Level.EXAMPLE));
    }

    @Override
    protected void update(float delta) {
        game.engine.update(delta);
    }
}
