package gsbs.common.data;

import gsbs.common.components.Hitbox;
import gsbs.common.components.Position;
import gsbs.common.components.Sprite;
import gsbs.common.entities.Asteroid;
import gsbs.common.entities.Entity;
import gsbs.common.math.Vector2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GridTest {
    private int nodeSize;
    private int displayHeight;
    private  int displayWidth;
    private GameData gameData;
    private World world;
    private Grid grid;
    @BeforeEach
    void setUp(){
        // create fresh standard grid with a world and gamedata
        this.nodeSize = 10;
        this.displayWidth = 800;
        this.displayHeight = 600;

        this.grid = new Grid(nodeSize,displayWidth, displayHeight);
        this.gameData = new GameData(0);
        this.gameData.setGrid(grid);
        this.world = new World();
    }


    @Test
    void testGridCreation(){
        // Assert no null nodes
        for (Node node: grid.getGrid()) {
            assertNotNull(node);
        }

        // Assert grid size
        assertEquals(grid.maxRow, displayHeight / nodeSize);
        assertEquals(grid.maxColumn, displayWidth / nodeSize);


        // Assert nothing blocked, collidable or has something other than a 0, 0 vector before update
        for (Node node: grid.getGrid()) {
            assertFalse(node.isBlocked());
            assertFalse(node.isCollidable());
            assertEquals(node.getCollisionVector(), new Vector2(0,0));
        }
    }

    @Test
    void testUpdateGrid(){
        // update the grid
        grid.updateGrid(world);

        // assert that it only updates once
        assertFalse(grid.updateGridFlag);

        boolean testFlag = false;
        for (Node node: grid.getGrid()) {

            if (node.isBlocked() || node.isCollidable()){
                testFlag = true;
            }

            // Assert that all
            if (node.isCollidable()) {
                assertNotEquals(node.getCollisionVector(), new Vector2(0, 0));
            }
        }

        // Assert that at least something has become collidable or blocked
        assertTrue(testFlag);


        // Create asteroid and update the grid
        Entity asteroid = new Asteroid();
        asteroid.add(new Hitbox(50, 50, 100, 100));
        asteroid.add(new Position(200, 200, 0));
        asteroid.add(new Sprite(null, 150, 150));
        world.addEntity(asteroid);
        grid.updateGridFlag = true;
        grid.updateGrid(world);

        // Assert that asteroid caused a node to be blocked
        assertTrue(grid.getNodeFromCoords(200, 200).isBlocked());
    }
}