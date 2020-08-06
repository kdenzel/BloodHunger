package de.kswmd.bloodhunger;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Logger;
import de.kswmd.bloodhunger.screens.GameScreen;

public class BloodHungerGame extends Game {

	private AssetManager assetManager = new AssetManager();

	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		assetManager.getLogger().setLevel(Logger.DEBUG);
		InputMultiplexer im = new InputMultiplexer();
		Gdx.input.setInputProcessor(im);
		setScreen(new GameScreen(this));
	}

	@Override
	public void dispose() {
		super.dispose();
		assetManager.dispose();
	}

	public AssetManager getAssetManager() {
		return assetManager;
	}
}
