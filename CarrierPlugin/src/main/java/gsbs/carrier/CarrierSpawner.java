package gsbs.carrier;

import gsbs.common.components.*;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.data.enums.Teams;
import gsbs.common.entities.Carrier;
import gsbs.common.events.Event;
import gsbs.common.events.SpawnCarrier;
import gsbs.common.services.IEventListener;

import java.util.Random;

public class CarrierSpawner implements IEventListener {

    private void spawnCarrier(GameData gameData, World world, Teams team) {
        float deacceleration = 10;
        float acceleration = 50f;
        float maxSpeed = 50;
        float rotationSpeed = 0.4f;

        int maxSpawnRetries = 50;
        Random random = new Random();
        for (int spawnRetries = 0; spawnRetries < maxSpawnRetries; spawnRetries++) {
            // We want players to spawn in the left third of the screen and enemys to spawn in the right third
            var carrierPositionY = random.nextFloat() * gameData.getDisplayHeight();
            var carrierPositionX = random.nextFloat() * gameData.getDisplayWidth() / 3.f;

            if (team == Teams.ENEMY)
                carrierPositionX = gameData.getDisplayWidth() - carrierPositionX;

            Carrier carrier = new Carrier();
            carrier.add(new Position(carrierPositionX, carrierPositionY, (float) (random.nextFloat() * 2 * Math.PI)));
            carrier.add(new Sprite(getClass().getResource("/carrier.png"), 30, 30));
            carrier.add(new Health(3));
            carrier.add(new Hitbox(15));
            carrier.add(new Team(team));
            carrier.add(new Movement(deacceleration, acceleration, maxSpeed, rotationSpeed));

            // Check if there are any collisions with the world
            var collision = false;
            for (var collidable : world.getEntitiesWithComponents(Position.class, Hitbox.class)) {
                var collidablePosition = collidable.getComponent(Position.class);
                var collidableHitbox = collidable.getComponent(Hitbox.class);
                var carrierPosition = carrier.getComponent(Position.class);
                var carrierHitbox = carrier.getComponent(Hitbox.class);

                if (collidableHitbox.intersects(collidablePosition, carrierPosition, carrierHitbox)) {
                    collision = true;
                    break;
                }
            }

            if (!collision) {
                world.addEntity(carrier);
                break;
            }
        }
    }

    @Override
    public void onEvent(Event event, GameData gameData) {
        if (event instanceof SpawnCarrier) {
            spawnCarrier(gameData, ((SpawnCarrier) event).getWorld(), ((SpawnCarrier) event).getTeam());
        }
    }
}
