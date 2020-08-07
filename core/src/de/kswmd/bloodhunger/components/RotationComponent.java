package de.kswmd.bloodhunger.components;

import com.badlogic.ashley.core.Component;

public class RotationComponent implements Component {

    public float movementAngle;
    public float lookingAngle;

    public RotationComponent(float movementAngle) {
        this.movementAngle = movementAngle;
        this.lookingAngle = movementAngle;
    }

    public RotationComponent() {
    }
}
