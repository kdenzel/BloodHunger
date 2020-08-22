package de.kswmd.bloodhunger.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Screen;
import de.kswmd.bloodhunger.utils.LevelManager;

public class LevelExitComponent implements Component {

    public final Screen nextScreen;
    public final LevelManager.Level level;

    public LevelExitComponent(Screen nextScreen, LevelManager.Level level) {
        this.level = level;
        this.nextScreen = nextScreen;
    }

}
