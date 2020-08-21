package de.kswmd.bloodhunger.components;

import com.badlogic.ashley.core.Component;

public class ItemComponent implements Component {

    public enum ItemType{
        AMMO(PlayerComponent.Tool.NONE, "ammo"),
        FLASHLIGHT(PlayerComponent.Tool.FLASHLIGHT, "flashlight"),
        HANDGUN(PlayerComponent.Tool.HANDGUN,"handgun");

        public final PlayerComponent.Tool tool;
        public final String resourceImage;

        ItemType(PlayerComponent.Tool tool, String resourceImage){
            this.tool = tool;
            this.resourceImage = resourceImage;
        }
    }

    public final ItemType itemType;

    public ItemComponent(ItemType itemType){
        this.itemType = itemType;
    }

}
