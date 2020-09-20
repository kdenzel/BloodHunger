package de.kswmd.bloodhunger;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class ListTestAdapter extends Game {

	private Screen startScreen;

	public ListTestAdapter () {
	}

	public ListTestAdapter (Screen startScreen) {
		this.startScreen = startScreen;
	}

	@Override
	public void create () {
		setScreen(startScreen);
	}
}
