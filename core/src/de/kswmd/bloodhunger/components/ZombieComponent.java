package de.kswmd.bloodhunger.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.skins.ZombieSkin;

public class ZombieComponent implements Component {

    public enum FeetAnimationType {
        IDLE(1f, "feet/idle/zombie-idle", Animation.PlayMode.LOOP),
        MOVE(1 / 6f, "feet/walk/zombie-walk", Animation.PlayMode.LOOP);

        public final float initialFrameDuration;
        public String resource;
        public Animation.PlayMode playMode;

        FeetAnimationType(float initialFrameDuration, String resource, Animation.PlayMode playMode) {
            this.initialFrameDuration = initialFrameDuration;
            this.resource = resource;
            this.playMode = playMode;
        }
    }

    public enum BodyAnimationType {
        //None
        IDLE_NONE(1 / 3f, "none/idle/zombie-idle_none", Animation.PlayMode.LOOP),
        MOVE_NONE(1 / 6f, "none/move/zombie-move_none", Animation.PlayMode.LOOP);/*,
        MELEEATTACK_NONE(1 / 24f, "none/move/zombie-move_none", Animation.PlayMode.LOOP)*/;

        public final float initialFrameDuration;
        public final String resource;
        public final Animation.PlayMode playMode;

        BodyAnimationType(float initialFrameDuration, String resource, Animation.PlayMode playMode) {
            this.initialFrameDuration = initialFrameDuration;
            this.resource = resource;
            this.playMode = playMode;

        }
    }

    public ZombieComponent.FeetAnimationType feetAnimationType = ZombieComponent.FeetAnimationType.IDLE;
    private ZombieComponent.BodyAnimationType bodyAnimationType = BodyAnimationType.IDLE_NONE;

    public float speed = MathUtils.random(50f, 200f) * BloodHungerGame.UNIT_SCALE;
    public float health = MathUtils.random(50f, 200f);
    public float frustumAngle = 60;
    public float timer = 0;

    private ZombieSkin skin;

    public ZombieComponent() {
        this.skin = ZombieSkin.create("zombie_skin_default");
    }


    public ZombieComponent.BodyAnimationType getBodyAnimationType() {
        if (skin.getBodyAnimationSkinElement(bodyAnimationType).getAnimation().isAnimationFinished(timer)
                && skin.getBodyAnimationSkinElement(bodyAnimationType).getAnimation().getPlayMode().equals(Animation.PlayMode.NORMAL)) {
            bodyAnimationType = BodyAnimationType.IDLE_NONE;
        }
        ZombieComponent.BodyAnimationType bodyAnimationType;
        switch (feetAnimationType) {
            case IDLE:
                bodyAnimationType = BodyAnimationType.IDLE_NONE;
                break;
            default:
                bodyAnimationType = BodyAnimationType.MOVE_NONE;
                break;
        }
        this.bodyAnimationType = bodyAnimationType;
        return bodyAnimationType;
    }

    public ZombieSkin getSkin() {
        return skin;
    }

    public void update(float deltaTime) {
        this.timer += deltaTime % 100;
    }

}
