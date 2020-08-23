package de.kswmd.bloodhunger.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import de.kswmd.bloodhunger.Assets;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.ui.inventory.Inventory;

public class PlayerComponent implements Component {

    public enum FeetAnimationType {
        IDLE(1f, "Top_Down_Survivor_custom/feet/idle/survivor-idle", Animation.PlayMode.LOOP),
        MOVE_FORWARD(1 / 24f, "Top_Down_Survivor_custom/feet/walk/survivor-walk", Animation.PlayMode.LOOP),
        MOVE_BACKWARD(1 / 24f, "Top_Down_Survivor_custom/feet/walk/survivor-walk", Animation.PlayMode.LOOP_REVERSED),
        MOVE_LEFT(1 / 24f, "Top_Down_Survivor_custom/feet/strafe_left/survivor-strafe_left", Animation.PlayMode.LOOP),
        MOVE_RIGHT(1 / 24f, "Top_Down_Survivor_custom/feet/strafe_right/survivor-strafe_right", Animation.PlayMode.LOOP);

        public final Animation<TextureRegion> animation;
        private final float initialFrameDuration;

        FeetAnimationType(float initialFrameDuration, String resource, Animation.PlayMode playMode) {
            TextureAtlas atlas = BloodHungerGame.ASSET_MANAGER.get(Assets.TEXTURE_ATLAS_GAME_ANIMATIONS);
            this.initialFrameDuration = initialFrameDuration;
            this.animation = new Animation<>(initialFrameDuration, atlas.findRegions(resource), playMode);
        }

        public float getInitialFrameDuration(){
            return initialFrameDuration;
        }
    }

    public enum BodyAnimationType {
        IDLE_NONE(1/24f,"Top_Down_Survivor_custom/none/idle/survivor-idle_none",Animation.PlayMode.LOOP),
        MOVE_NONE(1/24f,"Top_Down_Survivor_custom/none/move/survivor-move_none",Animation.PlayMode.LOOP),
        MELEE_NONE(1/48f,"Top_Down_Survivor_custom/none/meleeattack/survivor-meleeattack_none",Animation.PlayMode.NORMAL),
        IDLE_FLASHLIGHT(1 / 24f, "Top_Down_Survivor_custom/flashlight/idle/survivor-idle_flashlight", Animation.PlayMode.LOOP),
        MOVE_FLASHLIGHT(1 / 24f, "Top_Down_Survivor_custom/flashlight/move/survivor-move_flashlight", Animation.PlayMode.LOOP),
        MELEE_FLASHLIGHT(1 / 48f, "Top_Down_Survivor_custom/flashlight/meleeattack/survivor-meleeattack_flashlight", Animation.PlayMode.NORMAL),
        IDLE_HANDGUN(1 / 24f, "Top_Down_Survivor_custom/handgun/idle/survivor-idle_handgun", Animation.PlayMode.LOOP),
        MOVE_HANDGUN(1 / 24f, "Top_Down_Survivor_custom/handgun/move/survivor-move_handgun", Animation.PlayMode.LOOP),
        SHOOT_HANDGUN(1 / 48f, "Top_Down_Survivor_custom/handgun/shoot/survivor-shoot_handgun", Animation.PlayMode.NORMAL),
        MELEE_HANDGUN(1 / 48f, "Top_Down_Survivor_custom/handgun/meleeattack/survivor-meleeattack_handgun", Animation.PlayMode.NORMAL),
        RELOAD_HANDGUN(1 / 24f, "Top_Down_Survivor_custom/handgun/reload/survivor-reload_handgun", Animation.PlayMode.NORMAL);


        public final Animation<TextureRegion> animation;
        private final Array<float[]> polygonVertices = new Array<>();
        private final Array<float[]> polygonVerticesTransformed = new Array<>();
        private final float initialFrameDuration;

        BodyAnimationType(float initialFrameDuration, String resource, Animation.PlayMode playMode) {
            this.initialFrameDuration = initialFrameDuration;
            TextureAtlas atlas = BloodHungerGame.ASSET_MANAGER.get(Assets.TEXTURE_ATLAS_GAME_ANIMATIONS);
            this.animation = new Animation<>(initialFrameDuration, atlas.findRegions(resource), playMode);
            FileHandle handle = Gdx.files.internal("animation/" + resource + ".poly");
            if (handle.exists()) {
                String fileContent = handle.readString();
                String[] lines = fileContent.split("\\r?\\n");
                for (String line : lines) {
                    String[] array = line.replaceAll("[{}]", "").split(",");
                    float[] vertices = new float[array.length];
                    for (int j = 0; j < array.length; j++) {
                        vertices[j] = Float.parseFloat(array[j]);
                    }
                    polygonVertices.add(vertices);
                }

            } else {
                //Create per default a square from 0,0 to width height
                polygonVertices.add(new float[]{0,0,1,0,1,1,0,1});
            }
            polygonVertices.forEach(v -> polygonVerticesTransformed.add(new float[v.length]));
        }

        public boolean hasPolygons() {
            return !polygonVertices.isEmpty();
        }

        public float[] getVertices(float time, float width, float height) {
            if (polygonVertices.isEmpty()) {
                return null;
            }
            float scale = ((float) animation.getKeyFrameIndex(time) / animation.getKeyFrames().length);
            int polygonFrame = (int) (polygonVertices.size * scale);


            float[] v = polygonVertices.get(polygonFrame);
            float[] tv = polygonVerticesTransformed.get(polygonFrame);
            for (int i = 0; i < v.length; i++) {
                if (i % 2 == 0) {
                    tv[i] = v[i] * width;
                } else {
                    tv[i] = v[i] * height;
                }
            }
            return tv;
        }

        public float[] getVertices(float time, DimensionComponent dimensionComponent) {
            return getVertices(time, dimensionComponent.width, dimensionComponent.height);
        }

        public float getInitialFrameDuration() {
            return initialFrameDuration;
        }
    }

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

    public FeetAnimationType feetAnimationType = FeetAnimationType.IDLE;
    private BodyAnimationType bodyAnimationType = BodyAnimationType.IDLE_FLASHLIGHT;

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

    public BodyAnimationType getBodyAnimationType() {
        if (bodyAnimationType.animation.isAnimationFinished(timer) && bodyAnimationType.animation.getPlayMode().equals(Animation.PlayMode.NORMAL)) {
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

    public void switchTool(Tool tool) {
        this.tool = tool;
    }

    public Tool getTool() {
        return tool;
    }
}
