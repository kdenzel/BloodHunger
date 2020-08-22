package de.kswmd.bloodhunger.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.systems.RenderingSystem;
import de.kswmd.bloodhunger.ui.inventory.Inventory;
import de.kswmd.bloodhunger.ui.inventory.InventorySlot;
import de.kswmd.bloodhunger.utils.Mapper;

public class PlayerComponent implements Component {

    public enum Tool {
        NONE(false, 0),
        FLASHLIGHT(false, -25 * BloodHungerGame.UNIT_SCALE),
        HANDGUN(true, -25 * BloodHungerGame.UNIT_SCALE);

        private final Vector2 position = new Vector2();
        private final Vector2 offset = new Vector2();
        private ToolStatus status = ToolStatus.IDLE;
        private boolean shoot;
        private float yOffset;

        Tool(boolean shoot, float yOffset) {
            this.yOffset = yOffset;
            this.shoot = shoot;
        }

        public boolean canShoot() {
            return shoot;
        }

        public ToolStatus getStatus() {
            return status;
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
            position.setZero().set(dc.originX + 1 * BloodHungerGame.UNIT_SCALE, yOffset);
            position.rotate(rc.lookingAngle);
            position.add(pc.x + dc.originX, pc.y + dc.originY);
            return position;
        }

        /**
         * returns the transformed offset without position
         *
         * @param dc Dimension
         * @param rc Rotation
         * @return transformed Vector2 offset
         */
        public Vector2 getTransformedToolOffset(DimensionComponent dc, RotationComponent rc) {
            offset.setZero().set(0, yOffset);
            offset.rotate(rc.lookingAngle);
            return offset;
        }
    }

    public enum ToolStatus {
        IDLE,
        SHOOT,
        RELOAD,
        MELEE_ATTACK
    }

    public float timer = 0;

    private Tool tool = Tool.NONE;

    public RenderingSystem.FeetAnimationType feetAnimationType = RenderingSystem.FeetAnimationType.IDLE;
    private RenderingSystem.BodyAnimationType bodyAnimationType = RenderingSystem.BodyAnimationType.IDLE_FLASHLIGHT;

    public final Inventory inventory;

    public PlayerComponent(Inventory  inventory) {
        this.inventory = inventory;
    }

    public void shoot() {
        if (tool.canShoot()) {
            timer = 0;
            tool.status = ToolStatus.SHOOT;
        }
    }

    public void reload() {
        if (tool.canShoot()) {
            timer = 0;
            tool.status = ToolStatus.RELOAD;
        }
    }

    public void meeleAttack() {
        timer = 0;
        tool.status = ToolStatus.MELEE_ATTACK;
    }

    public RenderingSystem.BodyAnimationType getBodyAnimationType() {
        if (bodyAnimationType.animation.isAnimationFinished(timer) && bodyAnimationType.animation.getPlayMode().equals(Animation.PlayMode.NORMAL)) {
            tool.status = ToolStatus.IDLE;
        }
        RenderingSystem.BodyAnimationType bodyAnimationType;
        switch (tool.status) {
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
                throw new IllegalStateException("No animation for weapon status " + tool.status);
        }
        this.bodyAnimationType = bodyAnimationType;
        return bodyAnimationType;
    }

    private RenderingSystem.BodyAnimationType getReload() {
        switch (tool) {
            case HANDGUN:
                bodyAnimationType = RenderingSystem.BodyAnimationType.RELOAD_HANDGUN;
                break;
            default:
                throw new IllegalStateException("No shooting bodyanimationtype found for " + tool);
        }
        return bodyAnimationType;
    }

    private RenderingSystem.BodyAnimationType getMeleeAttack() {
        switch (tool) {
            case NONE:
                bodyAnimationType = RenderingSystem.BodyAnimationType.MELEE_NONE;
                break;
            case FLASHLIGHT:
                bodyAnimationType = RenderingSystem.BodyAnimationType.MELEE_FLASHLIGHT;
                break;
            case HANDGUN:
                bodyAnimationType = RenderingSystem.BodyAnimationType.MELEE_HANDGUN;
                break;
            default:
                throw new IllegalStateException("No meeleattack bodyanimationtype found for " + tool);
        }
        return bodyAnimationType;
    }

    private RenderingSystem.BodyAnimationType getShoot() {
        RenderingSystem.BodyAnimationType bodyAnimationType;
        switch (tool) {
            case HANDGUN:
                bodyAnimationType = RenderingSystem.BodyAnimationType.SHOOT_HANDGUN;
                break;
            default:
                throw new IllegalStateException("No shooting bodyanimationtype found for " + tool);
        }
        return bodyAnimationType;
    }

    private RenderingSystem.BodyAnimationType getIdle() {
        RenderingSystem.BodyAnimationType bodyAnimationType;
        switch (tool) {
            case NONE:
                bodyAnimationType = RenderingSystem.BodyAnimationType.IDLE_NONE;
                break;
            case FLASHLIGHT:
                bodyAnimationType = RenderingSystem.BodyAnimationType.IDLE_FLASHLIGHT;
                break;
            case HANDGUN:
                bodyAnimationType = RenderingSystem.BodyAnimationType.IDLE_HANDGUN;
                break;
            default:
                throw new IllegalStateException("No idle bodyanimationtype found for " + tool);
        }
        return bodyAnimationType;
    }

    private RenderingSystem.BodyAnimationType getMove() {
        RenderingSystem.BodyAnimationType bodyAnimationType;
        switch (tool) {
            case NONE:
                bodyAnimationType = RenderingSystem.BodyAnimationType.MOVE_NONE;
                break;
            case FLASHLIGHT:
                bodyAnimationType = RenderingSystem.BodyAnimationType.MOVE_FLASHLIGHT;
                break;
            case HANDGUN:
                bodyAnimationType = RenderingSystem.BodyAnimationType.MOVE_HANDGUN;
                break;
            default:
                throw new IllegalStateException("No moving bodyanimationtype found for " + tool);
        }
        return bodyAnimationType;
    }

    public void switchTool(Tool tool) {
        this.tool = tool;
    }

    public Tool getTool() {
        return tool;
    }
}
