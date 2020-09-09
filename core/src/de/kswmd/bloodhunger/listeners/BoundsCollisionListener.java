package de.kswmd.bloodhunger.listeners;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import de.kswmd.bloodhunger.components.BoundsComponent;

public interface BoundsCollisionListener {

    void onTriangleCollide(BoundsComponent bc,
                           BoundsComponent other,
                           Polygon triangle,
                           Polygon otherTriangle,
                           Intersector.MinimumTranslationVector minimumTranslationVector,
                           int zlayer,
                           int triangleCount);

}
