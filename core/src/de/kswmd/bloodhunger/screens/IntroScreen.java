package de.kswmd.bloodhunger.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import de.kswmd.bloodhunger.Assets;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.components.LevelExitComponent;
import de.kswmd.bloodhunger.utils.LevelManager;

public class IntroScreen extends BaseScreen {

    public IntroScreen(BloodHungerGame game) {
        super(game);
    }

    private Animation<TextureRegion> tvScreenAnimation;
    private TextureRegion backgroudTextureRegion;
    private Image tvScreen = new Image();
    private Image windowImage = new Image();
    private TextureRegionDrawable tvScreenDrawable = new TextureRegionDrawable();
    private TextureRegionDrawable windowDrawable = new TextureRegionDrawable();

    private TextureAtlas scenes;

    private float timer;

    private String[] moderatorDialog = {
            "Blahblahblah Trump Blahblahblah Corona Blahblahblah Fake News Blahblahblah Virus Blahblahblah stay at home Blahblahblah\n...",
            "Even more blahblahblah",
            "... New Virus... China ...makes people aggressive.",
            "Oh, something unexpected happens..."
    };

    private String[] thinkingDialog = {
            "... hmmmm... the power is gone. Maybe i should have a look whats going on."
    };

    @Override
    protected void initialize() {
        Gdx.input.setCursorCatched(false);
        scenes = BloodHungerGame.ASSET_MANAGER.get(Assets.TEXTURE_ATLAS_SCENES);
        tvScreenAnimation = new Animation<>(1 / 4f, scenes.findRegions("intro/tv_image"), Animation.PlayMode.LOOP);
        backgroudTextureRegion = scenes.findRegion("intro/background");

        Group group = new Group();
        Image backgroundImage = new Image(backgroudTextureRegion);
        backgroundImage.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        group.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        group.addActor(backgroundImage);

        TextureRegion region = tvScreenAnimation.getKeyFrame(timer);
        tvScreenDrawable.setRegion(region);
        tvScreen.setSize((float) Gdx.graphics.getWidth() * ((float) region.getRegionWidth() / backgroudTextureRegion.getRegionWidth() + 0.02f),
                (float) Gdx.graphics.getHeight() * ((float) region.getRegionHeight() / backgroudTextureRegion.getRegionHeight() + 0.01f));
        tvScreen.setDrawable(tvScreenDrawable);
        tvScreen.setPosition(Gdx.graphics.getWidth() * 0.454f, Gdx.graphics.getHeight() * 0.652f);
        group.addActor(tvScreen);

        region = scenes.findRegion("intro/window_lights_on");
        windowDrawable.setRegion(region);
        windowImage.setDrawable(windowDrawable);
        windowImage.setSize((float) Gdx.graphics.getWidth() * ((float) region.getRegionWidth() / backgroudTextureRegion.getRegionWidth()),
                (float) Gdx.graphics.getHeight() * ((float) region.getRegionHeight() / backgroudTextureRegion.getRegionHeight()));

        windowImage.setPosition(Gdx.graphics.getWidth() * 0.08f, Gdx.graphics.getHeight() * 0.4f);
        group.addActor(windowImage);
        group.addAction(Actions.fadeOut(0));
        group.addAction(Actions.fadeIn(2));
        uiStage.addActor(group);

        Label textLabel = new Label(moderatorDialog[0], game.uiSkin);
        Button button = new TextButton("Next", game.uiSkin);
        Window window = new Window("TV-Moderator:", game.uiSkin);
        window.setMovable(false);
        window.add(textLabel);
        window.row();
        window.add(button);
        window.pack();
        window.setPosition(Gdx.graphics.getWidth() / 2f - window.getWidth() / 2f, 0);
        uiStage.addActor(window);

        button.addListener(new ClickListener() {
            int index = 1;
            boolean animationPlayed = false;

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (moderatorDialog.length - 1 == index) {
                    //Power failure sequence
                    textLabel.setText(moderatorDialog[index]);
                    button.setVisible(false);
                    windowDrawable.setRegion(scenes.findRegion("intro/window_lights_off"));
                    Action lightsOnAndOffAction = new Action() {
                        private boolean lightsOn = true;

                        @Override
                        public boolean act(float delta) {
                            if (lightsOn) {
                                windowDrawable.setRegion(scenes.findRegion("intro/window_lights_off"));
                                lightsOn = false;
                            } else {
                                windowDrawable.setRegion(scenes.findRegion("intro/window_lights_on"));
                                lightsOn = true;
                            }
                            return true;
                        }
                    };
                    SequenceAction sequenceAction = new SequenceAction();
                    for (int i = 0; i < 3; i++) {
                        sequenceAction.addAction(Actions.sequence(
                                Actions.fadeOut(0),
                                lightsOnAndOffAction,
                                Actions.delay(MathUtils.random() + 0.1f),
                                Actions.fadeIn(0),
                                lightsOnAndOffAction,
                                Actions.delay(MathUtils.random() + 0.1f)
                        ));
                    }
                    sequenceAction.addAction(lightsOnAndOffAction);
                    sequenceAction.addAction(Actions.fadeOut(0));
                    sequenceAction.addAction(new Action() {
                        @Override
                        public boolean act(float delta) {
                            button.setVisible(true);
                            return true;
                        }
                    });
                    tvScreen.addAction(sequenceAction);
                    index = 0;
                    window.getTitleLabel().setText("Player:");
                    animationPlayed = true;
                } else if (!animationPlayed) {
                    textLabel.setText(moderatorDialog[index]);
                    index++;
                } else {
                    if (thinkingDialog.length == index) {
                        button.setVisible(false);
                        Action switchLevelAction = new Action() {
                            @Override
                            public boolean act(float delta) {
                                game.setLevel(new LevelExitComponent(BloodHungerGame.SCREEN_GAME, LevelManager.Level.EXAMPLE));
                                return true;
                            }
                        };
                        group.addAction(Actions.sequence(
                                Actions.fadeOut(4),
                                switchLevelAction
                        ));
                    } else {
                        textLabel.setText(thinkingDialog[index]);
                        index++;
                    }
                }
            }
        });

    }

    @Override
    protected void update(float delta) {
        timer += delta;
        TextureRegion region = tvScreenAnimation.getKeyFrame(timer);
        tvScreenDrawable.setRegion(region);
    }

    @Override
    public void hide() {
        super.hide();
    }
}
