package de.kswmd.bloodhunger.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import de.kswmd.bloodhunger.BloodHungerGame;


public class DesktopLauncher {

    public static final boolean DEBUG = true;

    public static void main(String[] arg) {
        boolean generate = false;
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.forceExit = false;
        config.width = 1280;
        config.height = 1024;
        //config.vSyncEnabled = true;
        if (generate) {
            TexturePacker.Settings settings = new TexturePacker.Settings();
            settings.maxWidth = 1024;
            settings.maxHeight = 1024;
            settings.duplicatePadding = false;
            settings.filterMag = Texture.TextureFilter.Linear;
            settings.filterMin = Texture.TextureFilter.Linear;
            settings.paddingX = 0;
            settings.paddingY = 0;
            //settings.debug = true;
            //Scale everything down cause we render in synfig everything higher than needed
            settings.scale = new float[]{0.5f};
            TexturePacker.process(settings, "../../desktop/assets-raw/animations", "./atlas", "animations");
            settings.scale = new float[]{1};
            TexturePacker.process(settings, "../../desktop/assets-raw/particles", "./atlas", "particles");
            TexturePacker.process(settings, "../../desktop/assets-raw/images", "./atlas", "images");
        }

        new LwjglApplication(new BloodHungerGame(DEBUG), config);
    }
}
