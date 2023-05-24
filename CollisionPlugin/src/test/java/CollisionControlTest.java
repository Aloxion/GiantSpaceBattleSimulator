import gsbs.collision.CollisionControlSystem;
import gsbs.common.components.Hitbox;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.entities.Entity;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CollisionControlTest {

    @Mock
    CollisionControlSystem controlSystem;
    @Mock
    GameData gameData;
    @Mock
    World world;

    @Test
    public void hitBoxTest(){
        Hitbox collider = new Hitbox(1, 1, 1, 1);
        Hitbox collided = new Hitbox(1, 1, 1, 1);
        assertEquals(true, collider.intersects(collided));
    }

    @Test
    public void testSystem(@Mock Entity entity, @Mock Entity entity2){

        world.addEntity(entity);
        world.addEntity(entity2);

        controlSystem.process(gameData, world);

    }
}
