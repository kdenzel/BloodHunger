package de.kswmd.bloodhunger.components;

import com.badlogic.ashley.core.Component;
import de.kswmd.bloodhunger.skins.PlayerSkin;

public class PlayerSkinComponent implements Component {

    public final PlayerSkin skin;

    public PlayerSkinComponent(PlayerSkin skin) {
        this.skin = skin;
    }
}
