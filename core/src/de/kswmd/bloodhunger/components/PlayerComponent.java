package de.kswmd.bloodhunger.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import de.kswmd.bloodhunger.systems.RenderingSystem;

public class PlayerComponent implements Component {

    public enum Weapon {
        FLASHLIGHT(false),
        HANDGUN(true);

        private WeaponStatus status = WeaponStatus.IDLE;

        private boolean shoot;

        Weapon(boolean shoot) {
            this.shoot = shoot;
        }

        public boolean canShoot() {
            return shoot;
        }

    }

    private enum WeaponStatus {
        IDLE,
        SHOOT,
        RELOAD,
        MELEE_ATTACK
    }

    public float timer = 0;

    public Weapon weapon = Weapon.FLASHLIGHT;

    public RenderingSystem.FeetAnimationType feetAnimationType = RenderingSystem.FeetAnimationType.IDLE;
    private RenderingSystem.BodyAnimationType bodyAnimationType = RenderingSystem.BodyAnimationType.IDLE_FLASHLIGHT;

    public PlayerComponent() {
    }

    public void shoot() {
        if (weapon.canShoot()) {
            timer = 0;
            weapon.status = WeaponStatus.SHOOT;
        }
    }

    public void reload() {
        if (weapon.canShoot()) {
            timer = 0;
            weapon.status = WeaponStatus.RELOAD;
        }
    }

    public void meeleAttack() {
        timer = 0;
        weapon.status = WeaponStatus.MELEE_ATTACK;
    }

    public RenderingSystem.BodyAnimationType getBodyAnimationType() {
        if (bodyAnimationType.animation.isAnimationFinished(timer) && bodyAnimationType.animation.getPlayMode().equals(Animation.PlayMode.NORMAL)) {
            weapon.status = WeaponStatus.IDLE;
        }
        RenderingSystem.BodyAnimationType bodyAnimationType;
        switch (weapon.status) {
            case IDLE:
                switch (feetAnimationType) {
                    case IDLE:
                        bodyAnimationType = getIdle();
                        break;
                    default:
                        bodyAnimationType = getMove();
                        break;
                }
                break;
            case SHOOT:
                bodyAnimationType = getShoot();
                break;
            case MELEE_ATTACK:
                bodyAnimationType = getMeleeAttack();
                break;
            case RELOAD:
                bodyAnimationType = getReload();
                break;
            default:
                throw new IllegalStateException("No animation for weapon status " + weapon.status);
        }
        this.bodyAnimationType = bodyAnimationType;
        return bodyAnimationType;
    }

    private RenderingSystem.BodyAnimationType getReload() {
        switch (weapon) {
            case HANDGUN:
                bodyAnimationType = RenderingSystem.BodyAnimationType.RELOAD_HANDGUN;
                break;
            default:
                throw new IllegalStateException("No shooting bodyanimationtype found for " + weapon);
        }
        return bodyAnimationType;
    }

    private RenderingSystem.BodyAnimationType getMeleeAttack() {
        switch (weapon) {
            case FLASHLIGHT:
                bodyAnimationType = RenderingSystem.BodyAnimationType.MELEE_FLASHLIGHT;
                break;
            case HANDGUN:
                bodyAnimationType = RenderingSystem.BodyAnimationType.MELEE_HANDGUN;
                break;
            default:
                throw new IllegalStateException("No shooting bodyanimationtype found for " + weapon);
        }
        return bodyAnimationType;
    }

    private RenderingSystem.BodyAnimationType getShoot() {
        RenderingSystem.BodyAnimationType bodyAnimationType;
        switch (weapon) {
            case HANDGUN:
                bodyAnimationType = RenderingSystem.BodyAnimationType.SHOOT_HANDGUN;
                break;
            default:
                throw new IllegalStateException("No shooting bodyanimationtype found for " + weapon);
        }
        return bodyAnimationType;
    }

    private RenderingSystem.BodyAnimationType getIdle() {
        RenderingSystem.BodyAnimationType bodyAnimationType;
        switch (weapon) {
            case FLASHLIGHT:
                bodyAnimationType = RenderingSystem.BodyAnimationType.IDLE_FLASHLIGHT;
                break;
            case HANDGUN:
                bodyAnimationType = RenderingSystem.BodyAnimationType.IDLE_HANDGUN;
                break;
            default:
                throw new IllegalStateException("No idle bodyanimationtype found for " + weapon);
        }
        return bodyAnimationType;
    }

    private RenderingSystem.BodyAnimationType getMove() {
        RenderingSystem.BodyAnimationType bodyAnimationType;
        switch (weapon) {
            case FLASHLIGHT:
                bodyAnimationType = RenderingSystem.BodyAnimationType.MOVE_FLASHLIGHT;
                break;
            case HANDGUN:
                bodyAnimationType = RenderingSystem.BodyAnimationType.MOVE_HANDGUN;
                break;
            default:
                throw new IllegalStateException("No moving bodyanimationtype found for " + weapon);
        }
        return bodyAnimationType;
    }

}
