package de.kswmd.bloodhunger.components;

import com.badlogic.ashley.core.Component;

public class RotationComponent implements Component {

    public float angle;

    public RotationComponent(float angle) {
        this.angle = angle;
    }

    public RotationComponent() {
    }
}
