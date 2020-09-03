package de.kswmd.bloodhunger.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.components.DimensionComponent;
import de.kswmd.bloodhunger.components.PlayerComponent;
import de.kswmd.bloodhunger.components.PositionComponent;
import de.kswmd.bloodhunger.utils.Mapper;

public class UpdateShadersSystem extends IteratingSystem {

    private Camera camera;
    private ShaderProgram shaderProgram;
    private Vector2 playerOrigin = new Vector2();

    public UpdateShadersSystem(Camera camera, ShaderProgram shaderProgram){
        super(Family.all(PlayerComponent.class).get());
        this.shaderProgram = shaderProgram;
        this.camera = camera;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent positionComponent = Mapper.positionComponent.get(entity);
        DimensionComponent dimensionComponent = Mapper.dimensionComponent.get(entity);
        shaderProgram.bind();
        playerOrigin.set(positionComponent.getPosition()).add(dimensionComponent.originX,dimensionComponent.originY);
        shaderProgram.setUniformMatrix("u_worldView",camera.combined);
        shaderProgram.setUniformf("u_shadowPos", playerOrigin);
        shaderProgram.setUniformf("u_distance",BloodHungerGame.worldUnits(0.5f));
    }
}
