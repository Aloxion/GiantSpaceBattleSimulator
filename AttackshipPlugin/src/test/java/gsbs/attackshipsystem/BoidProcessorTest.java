package gsbs.attackshipsystem;

import gsbs.attackshipsystem.components.Boid;
import gsbs.common.components.Hitbox;
import gsbs.common.components.Position;
import gsbs.common.components.Team;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.data.enums.Teams;
import gsbs.common.entities.Attackship;
import gsbs.common.entities.Bullet;
import gsbs.common.entities.Entity;
import gsbs.common.events.SpawnAttackships;
import gsbs.common.math.Vector2;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class BoidProcessorTest {

    @Test
    void processTest() {
        // Create a boid processor, world, game-data
        BoidProcessor boidProcessor = new BoidProcessor();
        World world = new World();

        GameData gameData = spy(new GameData(0));
        doReturn(1000).when(gameData).getDisplayWidth();
        doReturn(1000).when(gameData).getDisplayHeight();
        doReturn(0.016f).when(gameData).getDeltaTime();

        // Create leader object
        Entity leader = new Entity();
        Vector2 leaderPosition = new Vector2(100, 100);
        leader.add(new Position(leaderPosition.x, leaderPosition.y, 0));
        leader.add(new Team(Teams.PLAYER));
        world.addEntity(leader);

        // Create asteroid
        Entity asteroid = new Entity();
        asteroid.add(new Hitbox(50, 50, 100, 100));
        asteroid.add(new Position(100, 100, 0));
        world.addEntity(asteroid);

        // Spawn boid
        gameData.addEvent(new SpawnAttackships(leader, world, 1));
        gameData.getEventManager().dispatchEvents(gameData, List.of(new AttackshipSpawner()));

        // Run for some time, and check invariants
        Vector2 lastBoidPosition = new Vector2();
        boolean lasersSpawned = false;
        for (int i = 0; i < 5000; i++) {
            boidProcessor.process(gameData, world);
            Entity attackShip = world.getEntities(Attackship.class).get(0);
            Vector2 newBoidPosition = attackShip.getComponent(Position.class).asVector();
            Hitbox newBoidHitbox = attackShip.getComponent(Hitbox.class);
            Boid newBoid = attackShip.getComponent(Boid.class);

            // The hitbox, position and boid must be in sync
            assertTrue(newBoidPosition.equals(new Vector2(newBoidHitbox.getX(), newBoidHitbox.getY())));
            assertTrue(newBoidPosition.equals(newBoid.position));

            // The boid cannot stay still
            assertFalse(newBoidPosition.equals(lastBoidPosition));

            // The boid must never intersect the Asteroid
            assertFalse(asteroid.getComponent(Hitbox.class).intersects(newBoidHitbox));

            // Check that the distance to the leader never goes above some amount
            float maxLeaderDistance = 200;
            assertTrue(newBoidPosition.subtract(leaderPosition).length() < maxLeaderDistance);

            if (world.getEntities(Bullet.class).size() != 0) {
                lasersSpawned = true;
            }
        }

        assertTrue(lasersSpawned);
    }
}