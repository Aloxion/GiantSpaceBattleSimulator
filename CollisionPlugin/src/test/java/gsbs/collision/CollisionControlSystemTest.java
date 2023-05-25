package gsbs.collision;

import gsbs.common.components.*;
import gsbs.common.data.*;
import gsbs.common.data.enums.Teams;
import gsbs.common.entities.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class CollisionControlSystemTest {

    private CollisionControlSystem controlSystem;
    private GameData gameData;
    private World world;


    @BeforeEach
    void setUp() {
        gameData = new GameData(0);
        world = new World();
        controlSystem = new CollisionControlSystem();
    }
    @AfterEach
    void teardown(){
        if(world.getEntities() != null){
            for (Entity entity: world.getEntities()) {
                world.removeEntity(entity);
            }
        }
    }

    @Test
    public void hitBoxTest(){
        Hitbox collider = new Hitbox(1, 1, 1, 1);
        Hitbox collided = new Hitbox(1, 1, 1, 1);
        assertTrue(collider.intersects(collided));
    }

    @Test
    public void bulletXshipTest(){

        Bullet bullet = new Bullet();
        bullet.add(new Hitbox(1,1,1,1));
        Flagship flagship = new Flagship();
        flagship.add(new Hitbox(1,1,1,1));

        assertTrue(flagship.getComponent(Hitbox.class).intersects(bullet.getComponent(Hitbox.class)));
    }

    @Test
    public void removesDeadEntities(){
        Bullet bullet = new Bullet();
        bullet.add(new Health(2));
        bullet.add(new Hitbox(1,1,1,1));
        bullet.add(new Team(Teams.PLAYER));
        Flagship enemy = new Flagship();
        enemy.add(new Health(1));
        enemy.add(new Hitbox(1,1,1,1));
        enemy.add(new Team(Teams.ENEMY));
        world.addEntity(bullet);
        world.addEntity(enemy);

        controlSystem.handleCollisionEntity(bullet,gameData, world);

        assertEquals(1, world.getEntities().size());
    }

    @Test
    public void collisionWithoutTeam(){
        Bullet bullet = new Bullet();
        bullet.add(new Health(2));
        bullet.add(new Hitbox(1,1,1,1));
        Flagship enemy = new Flagship();
        enemy.add(new Health(1));
        enemy.add(new Hitbox(1,1,1,1));
        world.addEntity(bullet);
        world.addEntity(enemy);
        controlSystem.handleCollisionEntity(bullet,gameData, world);

        assertEquals(1, world.getEntities().size());
    }

    @Test
    public void asteroidCollision(){
        Flagship player = new Flagship();
        player.add(new Health(2));
        player.add(new Hitbox(1,1,1,1));
        player.add(new Team(Teams.ENEMY));
        Asteroid asteroid = new Asteroid();
        asteroid.add(new Hitbox(1,1,1,1));

        world.addEntity(player);
        world.addEntity(asteroid);
        controlSystem.handleCollisionEntity(player,gameData, world);

        assertEquals(1,player.getComponent(Health.class).getHealthPoints());
    }

}
