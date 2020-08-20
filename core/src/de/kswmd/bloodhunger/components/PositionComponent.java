package de.kswmd.bloodhunger.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class PositionComponent implements Component {

    public float x;
    public float y;
    private final Vector2 position = new Vector2();

    public PositionComponent() {
    }

    public PositionComponent(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void moveBy(float x, float y) {
        if (x != 0 || y != 0) {
            this.x += x;
            this.y += y;
        }
    }

    public void set(Vector2 vector2) {
        this.x = vector2.x;
        this.y = vector2.y;
    }

    public void set(float x, float y){
        this.x = x;
        this.y = y;
    }

    public Vector2 getPosition() {
        position.set(x, y);
        return position;
    }

    public void add(float x, float y) {
        this.x += x;
        this.y += y;
    }

    public void sub(float x, float y) {
        this.x -= x;
        this.y -= y;
    }
}
