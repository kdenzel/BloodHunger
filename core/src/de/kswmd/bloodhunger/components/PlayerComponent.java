package de.kswmd.bloodhunger.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.skins.PlayerSkin;
import de.kswmd.bloodhunger.ui.inventory.Inventory;

public class PlayerComponent implements Component {

    public enum FeetAnimationType {
        IDLE(1f, "feet/idle/survivor-idle", Animation.PlayMode.LOOP),
        MOVE_FORWARD(1 / 24f, "feet/walk/survivor-walk", Animation.PlayMode.LOOP),
        MOVE_BACKWARD(1 / 24f, "feet/walk/survivor-walk", Animation.PlayMode.LOOP_REVERSED),
        MOVE_LEFT(1 / 24f, "feet/strafe_left/survivor-strafe_left", Animation.PlayMode.LOOP),
        MOVE_RIGHT(1 / 24f, "feet/strafe_right/survivor-strafe_right", Animation.PlayMode.LOOP);

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
        IDLE_NONE(1 / 24f, "none/idle/survivor-idle_none", Animation.PlayMode.LOOP),
        MOVE_NONE(1 / 24f, "none/move/survivor-move_none", Animation.PlayMode.LOOP),
        MELEE_NONE(1 / 48f, "none/meleeattack/survivor-meleeattack_none", Animation.PlayMode.NORMAL),
        //Flashlight
        IDLE_FLASHLIGHT(1 / 24f, "flashlight/idle/survivor-idle_flashlight", Animation.PlayMode.LOOP),
        MOVE_FLASHLIGHT(1 / 24f, "flashlight/move/survivor-move_flashlight", Animation.PlayMode.LOOP),
        MELEE_FLASHLIGHT(1 / 48f, "flashlight/meleeattack/survivor-meleeattack_flashlight", Animation.PlayMode.NORMAL),
        //Handgun
        IDLE_HANDGUN(1 / 24f, "handgun/idle/survivor-idle_handgun", Animation.PlayMode.LOOP),
        MOVE_HANDGUN(1 / 24f, "handgun/move/survivor-move_handgun", Animation.PlayMode.LOOP),
        SHOOT_HANDGUN(1 / 48f, "handgun/shoot/survivor-shoot_handgun", Animation.PlayMode.NORMAL),
        MELEE_HANDGUN(1 / 48f, "handgun/meleeattack/survivor-meleeattack_handgun", Animation.PlayMode.NORMAL),
        RELOAD_HANDGUN(1 / 24f, "handgun/reload/survivor-reload_handgun", Animation.PlayMode.NORMAL);

        public final float initialFrameDuration;
        public final String resource;
        public final Animation.PlayMode playMode;

        BodyAnimationType(float initialFrameDuration, String resource, Animation.PlayMode playMode) {
            this.initialFrameDuration = initialFrameDuration;
            this.resource = resource;
            this.playMode = playMode;

        }
    }

    public enum Tool {
        NONE(false),
        FLASHLIGHT(false),
        HANDGUN(true);

        private final Vector2 position = new Vector2();
        private final Vector2 offset = new Vector2();
        private ToolStatus status = ToolStatus.IDLE;
        private final boolean shoot;

        Tool(boolean shoot) {
            this.shoot = shoot;
        }

        public boolean canShoot() {
            return shoot;
        }

        public ToolStatus getStatus() {
            return status;
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

    public FeetAnimationType feetAnimationType = FeetAnimationType.IDLE;
    private BodyAnimationType bodyAnimationType = BodyAnimationType.IDLE_FLASHLIGHT;

    public final Inventory inventory;
    private PlayerSkin skin;

    public PlayerComponent(Inventory inventory) {
        this.inventory = inventory;
        setSkin(PlayerSkin.create("player_skin_civil",-10f*BloodHungerGame.UNIT_SCALE,-20f * BloodHungerGame.UNIT_SCALE,false));
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

    public BodyAnimationType getBodyAnimationType() {
        if (skin.getBodyAnimationSkinElement(bodyAnimationType).getAnimation().isAnimationFinished(timer)
                && skin.getBodyAnimationSkinElement(bodyAnimationType).getAnimation().getPlayMode().equals(Animation.PlayMode.NORMAL)) {
            tool.status = ToolStatus.IDLE;
        }
        BodyAnimationType bodyAnimationType;
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

    private BodyAnimationType getReload() {
        switch (tool) {
            case HANDGUN:
                bodyAnimationType = BodyAnimationType.RELOAD_HANDGUN;
                break;
            default:
                throw new IllegalStateException("No shooting bodyanimationtype found for " + tool);
        }
        return bodyAnimationType;
    }

    private BodyAnimationType getMeleeAttack() {
        switch (tool) {
            case NONE:
                bodyAnimationType = BodyAnimationType.MELEE_NONE;
                break;
            case FLASHLIGHT:
                bodyAnimationType = BodyAnimationType.MELEE_FLASHLIGHT;
                break;
            case HANDGUN:
                bodyAnimationType = BodyAnimationType.MELEE_HANDGUN;
                break;
            default:
                throw new IllegalStateException("No meeleattack bodyanimationtype found for " + tool);
        }
        return bodyAnimationType;
    }

    private BodyAnimationType getShoot() {
        BodyAnimationType bodyAnimationType;
        switch (tool) {
            case HANDGUN:
                bodyAnimationType = BodyAnimationType.SHOOT_HANDGUN;
                break;
            default:
                throw new IllegalStateException("No shooting bodyanimationtype found for " + tool);
        }
        return bodyAnimationType;
    }

    private BodyAnimationType getIdle() {
        BodyAnimationType bodyAnimationType;
        switch (tool) {
            case NONE:
                bodyAnimationType = BodyAnimationType.IDLE_NONE;
                break;
            case FLASHLIGHT:
                bodyAnimationType = BodyAnimationType.IDLE_FLASHLIGHT;
                break;
            case HANDGUN:
                bodyAnimationType = BodyAnimationType.IDLE_HANDGUN;
                break;
            default:
                throw new IllegalStateException("No idle bodyanimationtype found for " + tool);
        }
        return bodyAnimationType;
    }

    private BodyAnimationType getMove() {
        BodyAnimationType bodyAnimationType;
        switch (tool) {
            case NONE:
                bodyAnimationType = BodyAnimationType.MOVE_NONE;
                break;
            case FLASHLIGHT:
                bodyAnimationType = BodyAnimationType.MOVE_FLASHLIGHT;
                break;
            case HANDGUN:
                bodyAnimationType = BodyAnimationType.MOVE_HANDGUN;
                break;
            default:
                throw new IllegalStateException("No moving bodyanimationtype found for " + tool);
        }
        return bodyAnimationType;
    }

    public PlayerSkin getSkin() {
        return skin;
    }

    public void setSkin(PlayerSkin skin){
        this.skin = skin;
        this.inventory.setBackpack(skin.backpack);
    }

    public void switchTool(Tool tool) {
        this.tool = tool;
    }

    public Tool getTool() {
        return tool;
    }


    public void update(float deltaTime) {
        this.timer += deltaTime % 100;
    }

}
