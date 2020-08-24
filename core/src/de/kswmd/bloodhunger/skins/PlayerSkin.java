package de.kswmd.bloodhunger.skins;

import com.badlogic.gdx.utils.Array;
import de.kswmd.bloodhunger.components.PlayerComponent;

public class PlayerSkin {

    public final Array<SkinElement> feetAnimations = new Array<>(PlayerComponent.FeetAnimationType.values().length);
    public final Array<SkinElement> bodyAnimations = new Array<>(PlayerComponent.BodyAnimationType.values().length);

    private PlayerSkin() {
    }

    /**
     * Creates a Skinobject for every animationtype defined in Playercomponent
     * @param skin, the skin top folder
     * @return
     */
    public static PlayerSkin create(String skin) {
        //Create PlayerSkin
        PlayerSkin playerSkin = new PlayerSkin();
        //Initialize FeetAnimations
        PlayerComponent.FeetAnimationType[] feetAnimationTypes = PlayerComponent.FeetAnimationType.values();
        for (int i = 0; i < feetAnimationTypes.length; i++) {
            PlayerComponent.FeetAnimationType feetAnimationType = feetAnimationTypes[i];
            playerSkin.feetAnimations.insert(i, new SkinElement(
                            skin,
                            feetAnimationType.initialFrameDuration,
                            feetAnimationType.resource, feetAnimationType.playMode
                    )
            );
        }
        //Initialize BodyAnimations
        PlayerComponent.BodyAnimationType[] bodyAnimationTypes = PlayerComponent.BodyAnimationType.values();
        for(int i = 0; i < bodyAnimationTypes.length; i++){
            PlayerComponent.BodyAnimationType bodyAnimationType = bodyAnimationTypes[i];
            playerSkin.bodyAnimations.insert(i,new SkinElement(
                    skin,
                    bodyAnimationType.initialFrameDuration,
                    bodyAnimationType.resource,
                    bodyAnimationType.playMode
            ));
        }
        return playerSkin;
    }

    public SkinElement getFeetAnimationSkinElement(PlayerComponent.FeetAnimationType feetAnimationType){
        return feetAnimations.get(feetAnimationType.ordinal());
    }

    public SkinElement getBodyAnimationSkinElement(PlayerComponent.BodyAnimationType bodyAnimationType){
        return bodyAnimations.get(bodyAnimationType.ordinal());
    }
}
