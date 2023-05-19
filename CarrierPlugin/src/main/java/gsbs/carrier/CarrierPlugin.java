package gsbs.carrier;

import gsbs.common.components.Hitbox;
import gsbs.common.components.Position;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.data.enums.Teams;
import gsbs.common.entities.Entity;
import gsbs.common.events.SpawnCarrier;
import gsbs.common.services.IPlugin;

public class CarrierPlugin implements IPlugin {
    private final int NUM_CARRIERS = 1;

    @Override
    public void start(GameData gameData, World world) {
        Entity entity = new Entity();
        entity.add(new Hitbox(50));
        entity.add(new Position(0, 0, (float) Math.PI * 0.25f));
        world.addEntity(entity);
        Entity entity2 = new Entity();
        entity2.add(new Hitbox(50));
        entity2.add(new Position(0, 0, 0));
        world.addEntity(entity2);
        for (int i = 0; i < NUM_CARRIERS; i++) {
            gameData.addEvent(new SpawnCarrier(null, world, Teams.PLAYER));
        }

        for (int i = 0; i < NUM_CARRIERS; i++) {
            gameData.addEvent(new SpawnCarrier(null, world, Teams.ENEMY));
        }
    }

    @Override
    public void stop(GameData gameData, World world) {

    }
}
