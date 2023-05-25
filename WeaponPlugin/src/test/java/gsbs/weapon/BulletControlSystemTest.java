package gsbs.weapon;

import gsbs.common.components.Health;
import gsbs.common.components.Hitbox;
import gsbs.common.components.Movement;
import gsbs.common.components.Position;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.entities.Bullet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BulletControlSystemTest {
    private BulletControlSystem bulletControlSystem;
    private GameData gameData;
    private World world;

    @BeforeEach
    void setUp() {
        bulletControlSystem = new BulletControlSystem();
        gameData = new GameData(0);
        world = new World();
    }

    @Test
    void process_bullet_removed_when_dead() {
        Bullet bullet = createTestBullet();
        world.addEntity(bullet);

        bulletControlSystem.process(gameData, world);

        Health health = bullet.getComponent(Health.class);
        assertEquals(9, health.getHealthPoints());

        //simulate 9 gameticks
        for (int i = 0; i < 9; i++) {
            bulletControlSystem.process(gameData, world);
        }

        assertTrue(health.isDead());
        assertFalse(world.getEntities(Bullet.class).contains(bullet));
    }

    @Test
    void process_bullet_move() {
        Bullet bullet = createTestBullet();
        world.addEntity(bullet);

        bulletControlSystem.process(gameData, world);

        Movement movement = bullet.getComponent(Movement.class);
        assertTrue(movement.isUp());
    }

    private Bullet createTestBullet() {
        Bullet bullet = new Bullet();
        bullet.add(new Health(10));
        bullet.add(new Movement(10, 10, 10, 10));
        bullet.add(new Hitbox(0, 0, 0, 0));
        bullet.add(new Position(0, 0, 0));
        return bullet;
    }
}