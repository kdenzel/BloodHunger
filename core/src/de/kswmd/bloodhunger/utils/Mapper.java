package de.kswmd.bloodhunger.utils;

import com.badlogic.ashley.core.ComponentMapper;
import de.kswmd.bloodhunger.components.*;

public final class Mapper {

    private Mapper(){}

    public static final ComponentMapper<AnimationComponent> animationComponent = ComponentMapper.getFor(AnimationComponent.class);
    public static final ComponentMapper<BoundsComponent> boundsComponent = ComponentMapper.getFor(BoundsComponent.class);
    public static final ComponentMapper<BulletComponent> bulletComponent = ComponentMapper.getFor(BulletComponent.class);
    public static final ComponentMapper<CenterCameraComponent> centerCameraComponent = ComponentMapper.getFor(CenterCameraComponent.class);
    public static final ComponentMapper<DimensionComponent> dimensionComponent = ComponentMapper.getFor(DimensionComponent.class);
    public static final ComponentMapper<EnemyComponent> enemyComponent = ComponentMapper.getFor(EnemyComponent.class);
    public static final ComponentMapper<FollowMouseComponent> followMouseComponent = ComponentMapper.getFor(FollowMouseComponent.class);
    public static final ComponentMapper<PlayerComponent> playerComponent = ComponentMapper.getFor(PlayerComponent.class);
    public static final ComponentMapper<PositionComponent> positionComponent = ComponentMapper.getFor(PositionComponent.class);
    public static final ComponentMapper<RoomComponent> roomComponent = ComponentMapper.getFor(RoomComponent.class);
    public static final ComponentMapper<RotationComponent> rotationComponent = ComponentMapper.getFor(RotationComponent.class);
    public static final ComponentMapper<TextureRegionComponent> textureRegionComponent = ComponentMapper.getFor(TextureRegionComponent.class);
    public static final ComponentMapper<TileComponent> tileComponent = ComponentMapper.getFor(TileComponent.class);
    public static final ComponentMapper<VelocityComponent> velocityComponent = ComponentMapper.getFor(VelocityComponent.class);
    public static final ComponentMapper<LightComponent> lightComponent = ComponentMapper.getFor(LightComponent.class);
    public static final ComponentMapper<FlashLightComponent> flashLightComponent = ComponentMapper.getFor(FlashLightComponent.class);
    public static final ComponentMapper<PlayerLightComponent> playerLightComponent = ComponentMapper.getFor(PlayerLightComponent.class);
    public static final ComponentMapper<ItemComponent> itemComponent = ComponentMapper.getFor(ItemComponent.class);
    public static final ComponentMapper<LevelExitComponent> levelExitComponent = ComponentMapper.getFor(LevelExitComponent.class);
    public static final ComponentMapper<PlayerSkinComponent> playerSkinComponent = ComponentMapper.getFor(PlayerSkinComponent.class);

}
