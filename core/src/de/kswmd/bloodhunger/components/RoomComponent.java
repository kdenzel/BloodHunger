package de.kswmd.bloodhunger.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import de.kswmd.bloodhunger.BloodHungerGame;
import de.kswmd.bloodhunger.factories.EntityFactory;
import de.kswmd.bloodhunger.screens.GameScreen;

public class RoomComponent implements Component {

    private final ComponentMapper<PositionComponent> cmpc = ComponentMapper.getFor(PositionComponent.class);
    private final ComponentMapper<TileComponent> cmtc = ComponentMapper.getFor(TileComponent.class);

    private final Engine engine;
    private final Entity[][] tiles;

    public RoomComponent(int tileSizeX, int tileSizeY, Engine engine, Entity roomEntity) {
        this.engine = engine;
        tiles = new Entity[tileSizeY][tileSizeX];
        for(int y = 0; y < tileSizeY; y++){
            for(int x = 0; x < tileSizeX; x++){
                PositionComponent pc = cmpc.get(roomEntity);
                Entity tile = EntityFactory.createTile(pc.x + x * BloodHungerGame.UNIT, pc.y + y *BloodHungerGame.UNIT);
                tiles[y][x] = tile;
            }
        }
    }
}
