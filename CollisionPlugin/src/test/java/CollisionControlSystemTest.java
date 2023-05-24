
import gsbs.collision.CollisionControlSystem;
import gsbs.common.components.Health;
import gsbs.common.components.Hitbox;
import gsbs.common.components.Team;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.data.enums.Teams;
import gsbs.common.entities.Entity;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CollisionControlSystemTest {

    @Mock
    CollisionControlSystem controlSystem;
    @Mock
    GameData mockGameData;
    @Mock
    World mockWorld;

    private Health createEntites (

            String id1,
            float width1,
            float height1,
            Teams team1,
            float ent1x,
            float ent1y,

            String id2,
            float width2,
            float height2,
            Teams team2,
            float ent2x,
            float ent2y

    ){
        Entity entity1 = mock(Entity.class);
        Health entity1Health = mock(Health.class);
        Hitbox hitbox1 = mock(Hitbox.class);
        team1 = mock(Teams.class);


        when(entity1.getID()).thenReturn(id1);
        when(entity1.getComponent(Health.class)).thenReturn(entity1Health); //Get HealthComponent
        when(entity1.getComponent(Hitbox.class)).thenReturn(hitbox1); //Get Hitbox component


        Entity entity2 = mock(Entity.class);
        Health entity2Health = mock(Health.class);
        Hitbox hitbox2 = mock(Hitbox.class);

        when(entity2.getID()).thenReturn(id2);
        when(entity2.getComponent(Health.class)).thenReturn(entity2Health); //Get HealthComponent
        when(entity2.getComponent(Hitbox.class)).thenReturn(hitbox2); //Get Hitbox component

        List<Entity> entityList = new LinkedList<>();
        entityList.add(entity1);
        entityList.add(entity2);
        when(mockWorld.getEntities()).thenReturn(entityList);

        return entity1Health;
    }


    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {

    }

    @AfterEach
    public void tearDown() {
    }


    @Test
    public void testCollides() {



    }

    @Test
    public void noCollision(){

    }

}
