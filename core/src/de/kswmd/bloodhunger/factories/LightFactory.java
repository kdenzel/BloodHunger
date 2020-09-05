package de.kswmd.bloodhunger.factories;

import box2dLight.*;
import com.badlogic.gdx.graphics.Color;
import de.kswmd.bloodhunger.BloodHungerGame;

public final class LightFactory {

    private LightFactory() {
    }

    public static Light createFlashLight(RayHandler rayHandler) {
        Light flashLight = new ConeLight(
                rayHandler, 50, null, 5 * BloodHungerGame.UNIT * BloodHungerGame.UNIT_SCALE, 0, 0, 0, 45
        );
        setDefaultLightSettings(flashLight);
        return flashLight;
    }

    public static Light createPlayerLight(RayHandler rayHandler) {
        return createPointLight(rayHandler, 4, null, BloodHungerGame.UNIT * BloodHungerGame.UNIT_SCALE, 0, 0);
    }

    public static Light createPointLight(RayHandler rayHandler, int rays, Color c, float distance, float x ,float y){
        Light light = new PointLight(rayHandler,rays,c,distance,x,y);
        setDefaultLightSettings(light);
        return light;
    }

    public static Light createConeLight(RayHandler rayHandler, int rays, Color c, float x, float y, float distance, float directionDegree, float coneDegree) {
        Light light = new ConeLight(rayHandler, rays, c, distance, x, y, directionDegree, coneDegree);
        setDefaultLightSettings(light);
        return light;
    }

    public static Light createDirectionalLight(RayHandler rayHandler, int rays, Color c,float directionDegree,float height) {
        Light light = new DirectionalLight(rayHandler, rays, c, directionDegree);
        setDefaultLightSettings(light);
        light.setHeight(height);
        light.setContactFilter(Box2DBodyFactory.CATEGORY_LIGHT, (short) 0, (short) (Box2DBodyFactory.CATEGORY_BOUNDARY|Box2DBodyFactory.CATEGORY_ROOF));
        return light;
    }

    private static void setDefaultLightSettings(Light light){
        //i am category light and i am collide with category boundary per default, ignoring all others
        light.setContactFilter(Box2DBodyFactory.CATEGORY_LIGHT, (short) 0, (short) (Box2DBodyFactory.CATEGORY_BOUNDARY));
        light.setSoftnessLength(0);
        light.setSoft(false);
        light.setHeight(1);
    }
}
