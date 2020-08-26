package de.kswmd.bloodhunger.skins;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.components.DimensionComponent;
import de.kswmd.bloodhunger.components.PlayerComponent;
import de.kswmd.bloodhunger.components.PositionComponent;
import de.kswmd.bloodhunger.components.RotationComponent;

public class PlayerSkin {

    private final Vector2 weaponOffset = new Vector2();
    private final Vector2 weaponPosition = new Vector2();
    private final float xWeaponOffset;
    private final float yWeaponOffset;
    public final Array<SkinElement> feetAnimations = new Array<>(PlayerComponent.FeetAnimationType.values().length);
    public final Array<SkinElement> bodyAnimations = new Array<>(PlayerComponent.BodyAnimationType.values().length);

    private PlayerSkin(float xWeaponOffset, float yWeaponOffset) {
        this.xWeaponOffset = xWeaponOffset;
        this.yWeaponOffset = yWeaponOffset;
        weaponOffset.set(xWeaponOffset,yWeaponOffset);
    }

    /**
     * Creates a Skinobject for every animationtype defined in Playercomponent
     * @param skin, the skin top folder
     * @return The Skin for the player
     */
    public static PlayerSkin create(String skin, float xWeaponOffset, float yWeaponOffset) {
        //Create PlayerSkin
        PlayerSkin playerSkin = new PlayerSkin(xWeaponOffset,yWeaponOffset);
        //Initialize FeetAnimations
        PlayerComponent.FeetAnimationType[] feetAnimationTypes = PlayerComponent.FeetAnimationType.values();
        for (int i = 0; i < feetAnimationTypes.length; i++) {
            PlayerComponent.FeetAnimationType feetAnimationType = feetAnimationTypes[i];
            playerSkin.feetAnimations.insert(i, new SkinElement(
                            skin,
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


    /*
     * Returns the transformed position with offset of the weapon, for example the flashlight is not centered and instead a little on the right/left,
     * the Position of the light cone will be a little offset. The same for guns and bullets.
     *
     * @param pc Position
     * @param dc Dimension
     * @param rc Rotation
     * @return
     */
    public Vector2 getTransformedToolPositionWithOffset(PositionComponent pc, DimensionComponent dc, RotationComponent rc) {
        weaponPosition.setZero().set(dc.originX + 1 * BloodHungerGame.UNIT_SCALE + xWeaponOffset, yWeaponOffset);
        weaponPosition.rotate(rc.lookingAngle);
        weaponPosition.add(pc.x + dc.originX, pc.y + dc.originY);
        return weaponPosition;
    }

    /**
     * returns the transformed offset without position
     *
     * @param dc Dimension
     * @param rc Rotation
     * @return transformed Vector2 offset
     */
    public Vector2 getTransformedToolOffset(DimensionComponent dc, RotationComponent rc) {
        weaponOffset.setZero().set(xWeaponOffset, yWeaponOffset);
        weaponOffset.rotate(rc.lookingAngle);
        return weaponOffset;
    }
}
