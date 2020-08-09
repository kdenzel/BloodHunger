package de.kswmd.bloodhunger.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import de.kswmd.bloodhunger.BloodHungerGame;

public class DesktopLauncher {
    public static void main(String[] arg) {
        boolean generate = false;
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.forceExit = false;
        config.width = 1920;
        config.height = 1080;
        if (generate) {
            TexturePacker.Settings settings = new TexturePacker.Settings();
            settings.maxWidth = 4096;
            settings.maxHeight = 4096;
            settings.duplicatePadding = false;
            settings.filterMag = Texture.TextureFilter.Linear;
            settings.filterMin = Texture.TextureFilter.Linear;
            TexturePacker.process(settings, "../../desktop/assets-raw", "./", "bloodHunger");
        }

        new LwjglApplication(new BloodHungerGame(), config);
    }
}
