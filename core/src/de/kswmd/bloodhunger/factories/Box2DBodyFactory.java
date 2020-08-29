package de.kswmd.bloodhunger.factories;

import com.badlogic.gdx.physics.box2d.*;
import de.kswmd.bloodhunger.BloodHungerGame;

public final class Box2DBodyFactory {

    public static final short CATEGORY_BOUNDARY = 0x001;
    public static final short CATEGORY_LIGHT = 0x002;
    public static final short CATEGORY_IGNORE = 0x004;

    private Box2DBodyFactory() {
    }

    public static Body createKinematicRectanglePolygonBody(float width, float height,short category) {
        // First we create a body definition
        BodyDef bodyDef = new BodyDef();
// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
        bodyDef.type = BodyDef.BodyType.KinematicBody;
// Create our body in the world using our body definition
        Body body = BloodHungerGame.WORLD.createBody(bodyDef);

// Create a rectangle polygon shape
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width / 2, height / 2);
        //polygonShape.setAsBox(x,y,new Vector2(width/2,height/2),0);
// Create a fixture definition to apply our shape to
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = category;

// Create our fixture and attach it to the body
        Fixture fixture = body.createFixture(fixtureDef);
// Remember to dispose of any shapes after you're done with them!
// BodyDef and FixtureDef don't need disposing, but shapes do.
        polygonShape.dispose();
        return body;
    }

    /**
     * Only between 3 and 8 vertices possible in box2d
     * @param vertices - the vertices of the polygon
     * @return the box2d bodyobject with the polygon shape
     */
    public static Body createKinematicPolygonBody(float[] vertices) {
        // First we create a body definition
        BodyDef bodyDef = new BodyDef();
// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
        bodyDef.type = BodyDef.BodyType.KinematicBody;
// Create our body in the world using our body definition
        Body body = BloodHungerGame.WORLD.createBody(bodyDef);

// Create a rectangle polygon shape
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(vertices);
        //polygonShape.setAsBox(x,y,new Vector2(width/2,height/2),0);
// Create a fixture definition to apply our shape to
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density = 1;
// Create our fixture and attach it to the body
        Fixture fixture = body.createFixture(fixtureDef);
// Remember to dispose of any shapes after you're done with them!
// BodyDef and FixtureDef don't need disposing, but shapes do.
        polygonShape.dispose();
        return body;
    }
}
