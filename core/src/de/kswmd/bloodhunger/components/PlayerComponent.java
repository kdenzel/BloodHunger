package de.kswmd.bloodhunger.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import de.kswmd.bloodhunger.systems.RenderingSystem;

public class PlayerComponent implements Component {

    public float timer = 0;

    public RenderingSystem.FeetAnimationType feetAnimationType = RenderingSystem.FeetAnimationType.IDLE;
    public RenderingSystem.BodyAnimationType bodyAnimationType = RenderingSystem.BodyAnimationType.IDLE_FLASHLIGHT;

    public PlayerComponent(AssetManager manager){
        TextureAtlas atlas = manager.get("bloodHunger.atlas");
    }


}
