package de.kswmd.bloodhunger.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import de.kswmd.bloodhunger.BloodHungerGame;


public class DesktopLauncher {

    public static final boolean DEBUG = true;
    public static final boolean CREATE = false;

    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.forceExit = false;
        config.width = 1920;
        config.height = 1080;
        config.fullscreen = false;
        //Enables max fps
        /*config.foregroundFPS = -1;
        config.backgroundFPS = -1;
        config.vSyncEnabled = false;*/
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.duplicatePadding = false;
        settings.filterMag = Texture.TextureFilter.Linear;
        settings.filterMin = Texture.TextureFilter.Linear;
        settings.maxHeight = 4096;
        settings.maxWidth = 4096;
        if (CREATE) {
            //settings.debug = true;
            //Scale everything down cause we render in synfig everything higher than needed
            settings.scale = new float[]{0.5f};
            TexturePacker.process(settings, "../../desktop/assets-raw/game_animations/animations", "./atlas", "animations");
            TexturePacker.process(settings, "../../desktop/assets-raw/scenes", "./atlas", "scenes");
            //Do not scale this, it isn't rendered with synfig
            settings.scale = new float[]{1};
            TexturePacker.process(settings, "../../desktop/assets-raw/particles", "./atlas", "particles");
            TexturePacker.process(settings, "../../desktop/assets-raw/images", "./atlas", "images");
            TexturePacker.process(settings, "../../desktop/assets-raw/ui", "./ui", "uiskin");
        }

        new LwjglApplication(new BloodHungerGame(DEBUG), config);
    }
}
