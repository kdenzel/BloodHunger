package de.kswmd.bloodhunger.skins;

import de.kswmd.bloodhunger.components.PlayerComponent;
import de.kswmd.bloodhunger.components.ZombieComponent;

public class ZombieSkin extends Skin {

    private ZombieSkin(){

    }

    public static ZombieSkin create(String skin) {
        ZombieSkin zombieSkin = new ZombieSkin();
        //Initialize FeetAnimations
        ZombieComponent.FeetAnimationType[] feetAnimationTypes = ZombieComponent.FeetAnimationType.values();
        for (int i = 0; i < feetAnimationTypes.length; i++) {
            ZombieComponent.FeetAnimationType feetAnimationType = feetAnimationTypes[i];
            zombieSkin.feetAnimations.insert(i, new SkinElement(
                            skin,
                            feetAnimationType.resource, feetAnimationType.playMode
                    )
            );
        }
        //Initialize BodyAnimations
        ZombieComponent.BodyAnimationType[] bodyAnimationTypes = ZombieComponent.BodyAnimationType.values();
        for(int i = 0; i < bodyAnimationTypes.length; i++){
            ZombieComponent.BodyAnimationType bodyAnimationType = bodyAnimationTypes[i];
            zombieSkin.bodyAnimations.insert(i,new SkinElement(
                    skin,
                    bodyAnimationType.resource,
                    bodyAnimationType.playMode
            ));
        }
        return zombieSkin;
    }

    public SkinElement getFeetAnimationSkinElement(ZombieComponent.FeetAnimationType feetAnimationType){
        return feetAnimations.get(feetAnimationType.ordinal());
    }

    public SkinElement getBodyAnimationSkinElement(ZombieComponent.BodyAnimationType bodyAnimationType){
        return bodyAnimations.get(bodyAnimationType.ordinal());
    }

}
