package de.kswmd.bloodhunger.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PlayerAnimationComponent implements Component {

    public PlayerAnimationComponent(AssetManager manager){
        TextureAtlas atlas = manager.get("bloodHunger.atlas");
        animationFeet = new Animation<TextureRegion>(0.5f, atlas.findRegions("survivor-idle"), Animation.PlayMode.LOOP);
        animationBody = new Animation<TextureRegion>(0.5f, atlas.findRegions("survivor-idle_handgun"), Animation.PlayMode.LOOP);
    }

    public Animation<TextureRegion> animationFeet;
    public Animation<TextureRegion> animationBody;

}
