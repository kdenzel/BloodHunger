package de.kswmd.bloodhunger.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;

/**
 * Dimension for Textures
 */
public class DimensionComponent implements Component {

    public final float width;
    public final float height;
    public float scaleX = 1f;
    public float scaleY = 1f;
    public final float originX;
    public final float originY;

    public DimensionComponent(float width, float height, float scaleX, float scaleY) {
        this.width = width;
        this.height = height;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.originX = width / 2;
        this.originY = height / 2;
    }

    public DimensionComponent(float width, float height) {
        this.width = width;
        this.height = height;
        this.originX = width / 2;
        this.originY = height / 2;
    }
}
