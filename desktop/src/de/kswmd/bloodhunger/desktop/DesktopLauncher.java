package de.kswmd.bloodhunger.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import de.kswmd.bloodhunger.BloodHungerGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.forceExit = false;
		config.width = 1920;
		config.height = 1080;
		new LwjglApplication(new BloodHungerGame(), config);
	}
}
