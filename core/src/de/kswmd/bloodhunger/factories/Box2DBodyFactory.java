package de.kswmd.bloodhunger.factories;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import de.kswmd.bloodhunger.screens.GameScreen;

public final class Box2DBodyFactory {

    private Box2DBodyFactory() {
    }

    public static Body createKinematicRectangleBody(float x, float y, float width, float height) {
        // First we create a body definition
        BodyDef bodyDef = new BodyDef();
// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(width/2,height/2);
// Create our body in the world using our body definition
        Body body = GameScreen.WORLD.createBody(bodyDef);

// Create a rectangle polygon shape
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width/2,height/2);
        //polygonShape.setAsBox(x,y,new Vector2(width/2,height/2),0);
// Create a fixture definition to apply our shape to
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
// Create our fixture and attach it to the body
        Fixture fixture = body.createFixture(fixtureDef);
// Remember to dispose of any shapes after you're done with them!
// BodyDef and FixtureDef don't need disposing, but shapes do.
        polygonShape.dispose();
        return body;
    }
}
