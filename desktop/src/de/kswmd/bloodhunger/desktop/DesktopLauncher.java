package de.kswmd.bloodhunger.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import de.kswmd.bloodhunger.BloodHungerGame;


public class DesktopLauncher {

    public static final boolean DEBUG = false;

    public static void main(String[] arg) {
        boolean generate = false;
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.forceExit = false;
        config.width = 800;
        config.height = 600;
        config.fullscreen = false;
        //Enables max fps
        /*config.foregroundFPS = -1;
        config.backgroundFPS = -1;
        config.vSyncEnabled = false;*/
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.duplicatePadding = false;
        settings.filterMag = Texture.TextureFilter.Linear;
        settings.filterMin = Texture.TextureFilter.Linear;
        if (generate) {
            //settings.debug = true;
            //Scale everything down cause we render in synfig everything higher than needed
            settings.scale = new float[]{0.5f};
            TexturePacker.process(settings, "../../desktop/assets-raw/animations", "./atlas", "animations");
            settings.scale = new float[]{1};
            TexturePacker.process(settings, "../../desktop/assets-raw/particles", "./atlas", "particles");
            TexturePacker.process(settings, "../../desktop/assets-raw/images", "./atlas", "images");
            TexturePacker.process(settings, "../../desktop/assets-raw/ui", "./ui", "uiskin");
        }

        new LwjglApplication(new BloodHungerGame(DEBUG), config);
    }
}
