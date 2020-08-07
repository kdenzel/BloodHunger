package de.kswmd.bloodhunger.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class VelocityComponent implements Component {

    public final Vector2 velocityVec = new Vector2();

    public VelocityComponent() {
    }

    public VelocityComponent(float speed) {
        velocityVec.set(speed, 0);
    }

    public VelocityComponent(float speed, float angle) {
        velocityVec.set(speed, 0).setAngle(angle);
    }

}
