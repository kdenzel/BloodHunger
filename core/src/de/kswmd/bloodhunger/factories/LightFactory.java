package de.kswmd.bloodhunger.factories;

import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import de.kswmd.bloodhunger.BloodHungerGame;

public final class LightFactory {

    private LightFactory() {
    }

    public static Light createFlashLight(RayHandler rayHandler) {
        Light flashLight = new ConeLight(
                rayHandler, 50, null, 10 * BloodHungerGame.UNIT * BloodHungerGame.UNIT_SCALE, 0, 0, 0, 45
        );
        setDefaultLightSettings(flashLight);
        return flashLight;
    }

    public static Light createPlayerLight(RayHandler rayHandler) {
        Light light = new PointLight(rayHandler, 10, null, BloodHungerGame.UNIT * BloodHungerGame.UNIT_SCALE, 0, 0);
        setDefaultLightSettings(light);
        return light;
    }

    public static Light createConeLight(RayHandler rayHandler, int rays, Color c, float x, float y, float distance, float directionDegree, float coneDegree) {
        Light light = new ConeLight(rayHandler, rays, c, distance, x, y, directionDegree, coneDegree);
        setDefaultLightSettings(light);
        return light;
    }

    private static void setDefaultLightSettings(Light light){
        //i am category light and i am collide with category boundary per default, ignoring all others
        light.setContactFilter(Box2DBodyFactory.CATEGORY_LIGHT, (short) 0, Box2DBodyFactory.CATEGORY_BOUNDARY);
        light.setSoftnessLength(0);
    }
}
