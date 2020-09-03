package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import de.kswmd.bloodhunger.components.*;
import de.kswmd.bloodhunger.utils.Mapper;

public class CenterCameraSystem extends EntitySystem {

    private ImmutableArray<Entity> playerEntities;
    private ImmutableArray<Entity> crosshairs;
    private Camera camera;
    private Vector2 tmpVec = new Vector2();
    private Vector3 worldCoordsToScreenCoords = new Vector3();
    private float timer = 0;

    public CenterCameraSystem(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void addedToEngine(Engine engine) {
        playerEntities = engine.getEntitiesFor(Family.all(PlayerComponent.class).get());
        crosshairs = engine.getEntitiesFor(Family.all(FollowMouseComponent.class).get());
    }

    @Override
    public void removedFromEngine(Engine engine) {
        playerEntities = null;
        crosshairs = null;
    }

    @Override
    public void update(float deltaTime) {
        Entity player = playerEntities.first();
        Entity crosshair = crosshairs.first();

        PositionComponent playerPc = Mapper.positionComponent.get(player);
        DimensionComponent playerDc = Mapper.dimensionComponent.get(player);
        RotationComponent playerRc = Mapper.rotationComponent.get(player);

        PositionComponent crosshairPc = Mapper.positionComponent.get(crosshair);
        DimensionComponent crosshairDc = Mapper.dimensionComponent.get(crosshair);


        float playerCenterX = playerPc.x + playerDc.originX;
        float playerCenterY = playerPc.y + playerDc.originY;

        float crosshairCenterX = crosshairPc.x + crosshairDc.originX;
        float crosshairCenterY = crosshairPc.y + crosshairDc.originY;
        //Set it to our world center
        worldCoordsToScreenCoords.set(camera.viewportWidth, camera.viewportHeight, 0);
        camera.unproject(worldCoordsToScreenCoords);
        //remove camera position, so we get the middle of the screen coordinate.
        worldCoordsToScreenCoords.sub(camera.position.x, camera.position.y, 0);

        tmpVec.set(Math.min(Math.abs(worldCoordsToScreenCoords.x / 2),Math.abs(playerCenterX - crosshairCenterX)),Math.min(Math.abs(worldCoordsToScreenCoords.y / 2),Math.abs(playerCenterY - crosshairCenterY)));
        tmpVec.setAngle(playerRc.lookingAngle);
        //Reuse our Vector3 Object for setting the final position
        worldCoordsToScreenCoords.set(playerCenterX+tmpVec.x,playerCenterY+tmpVec.y,0);
        //Improtant, moves smoothly to the position. Lerp to it
        camera.position.lerp(worldCoordsToScreenCoords,deltaTime*2);
        camera.update();
    }

}
