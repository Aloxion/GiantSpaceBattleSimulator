package gsbs.weapon.implementations.pistol;

import gsbs.common.components.*;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.data.enums.Teams;
import gsbs.common.entities.Bullet;
import gsbs.common.entities.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PistolPluginTest {
    private PistolPlugin pistolPlugin;
    private GameData gameData;
    private World world;

    private Entity entity;

    @BeforeEach
    void setUp() {
        gameData = new GameData(0);
        world = new World();
        pistolPlugin = new PistolPlugin();
        entity = new Entity();
        entity.add(new Position(0,0,0));
        entity.add(new Team(Teams.PLAYER));
    }

    @Test
    void add_bullet_on_fire() {
        pistolPlugin.fire(entity, world);
        assertTrue(world.getEntities(Bullet.class).size() == 1);
    }

    @Test
    void bullet_faces_correct_way() {
        pistolPlugin.fire(entity, world);
        Position position = world.getEntities(Bullet.class).get(0).getComponent(Position.class);
        assertTrue(position.getRadians() == 0);
    }



    private Bullet createTestBullet() {
        Bullet bullet = new Bullet();
        bullet.add(new Health(10));
        bullet.add(new Position(0,0,0));
        bullet.add(new Movement(10, 10, 10, 10));
        bullet.add(new Hitbox(0,0,0,0));
        return bullet;
    }
}