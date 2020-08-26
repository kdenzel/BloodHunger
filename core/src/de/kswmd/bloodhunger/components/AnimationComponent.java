package de.kswmd.bloodhunger.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimationComponent implements Component {

    public float timer;
    public Animation<TextureRegion> animation;

    public void update(float deltaTime){
        this.timer += deltaTime % 100;
    }
}
