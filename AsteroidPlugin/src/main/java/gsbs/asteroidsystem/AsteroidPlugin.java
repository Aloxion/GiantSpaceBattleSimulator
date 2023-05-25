package gsbs.asteroidsystem;

import gsbs.common.components.Hitbox;
import gsbs.common.components.Position;
import gsbs.common.components.Sprite;
import gsbs.common.data.GameData;
import gsbs.common.data.AsteroidNode;
import gsbs.common.data.World;
import gsbs.common.data.enums.AsteroidSizes;
import gsbs.common.entities.Asteroid;
import gsbs.common.entities.Entity;
import gsbs.common.services.IPlugin;

import java.util.Random;

public class AsteroidPlugin implements IPlugin {
    private AsteroidNode[][] asteroidGrid;
    private final static int cellSize = 128;
    private final static int MAX_ATTEMPTS = 50;
    private static final Random getRandom = new Random();
    @Override
    public void start(GameData gameData, World world) {
        asteroidGrid = createAsteroidGrid(gameData);
        int attempts = 0;

        for (int i = 0; i < 16; i++) {
            Entity asteroid = createAsteroid();
            if (asteroid != null){
                //Update hitbox to make sure hitboxes are correct
                updateHitbox(asteroid);
                world.addEntity(asteroid);
                attempts = 0;
            } else if (attempts > MAX_ATTEMPTS) {
                //If asteroids amount are bigger than the grid size.
                break;
            } else {
                attempts += 1;
                i--;
            }
        }
    }

    @Override
    public void stop(GameData gameData, World world) {
        for (var asteroid : world.getEntities(Asteroid.class)) {
            world.removeEntity(asteroid);
        }
    }

    private Entity createAsteroid() {
        float radians = (float) (3.1415f / (Math.random() * 5));
        Entity asteroid = new Asteroid();
        AsteroidSizes sizeEnum = randomDirection();
        float size = sizeEnum.getSize();

        //Get number (rows, cols)
        int numRows = asteroidGrid.length;
        int numCols = asteroidGrid[0].length;

        // randomly select a starting node from within the grid, we use the grids number of col & rows,
        // to look for inner nodes.
        int randRow = (int) (Math.random() * (numRows/2) + 1);
        int randCol = (int) (Math.random() * (numCols/2) + 1);
        AsteroidNode currentNode = asteroidGrid[randRow][randCol];

        // Randomly selected a value between 0..3
        int direction = (int) (Math.random() * 4); // 0 = up, 1 = right, 2 = down, 3 = left

        //Declares if we move row or column.
        int rowStep = direction == 0 ? -1 : (direction == 2 ? 1 : 0); //Vertical spawn
        int colStep = direction == 1 ? 2 : (direction == 3 ? -1 : 0); //Horizontal spawn

        int currentRow = randRow;
        int currentCol = randCol;
        boolean onEdge = false;

        while (!onEdge) {
            // Moves according to rowStep or colStep
            currentRow += rowStep;
            currentCol += colStep;

            // Checks if the asteroid has moved off the edge of the grid, if so we won't go further
            if (currentRow < 0 || currentRow >= numRows || currentCol < 0 || currentCol >= numCols) {
                onEdge = true;
            } else {
                currentNode = asteroidGrid[currentRow][currentCol];
            }
            //Create new asteroid if currentNode is defined && if there is no entities in that node.
            if (currentNode != null && currentNode.getEntities().isEmpty()) {
                asteroid.add(new Position(currentNode.getCol()*cellSize, currentNode.getRow()*cellSize, radians));
                asteroid.add(new Sprite(getClass().getResource("/assets/default-asteroid.png"), (int) size, (int) size));
                asteroid.add(new Hitbox(size, size, currentNode.getCol()*cellSize, currentNode.getRow()*cellSize));
                currentNode.addEntity(asteroid);
                return asteroid;
            }
        }
        return null;
    }

    public AsteroidNode[][] createAsteroidGrid(GameData gameData) {
        //Sets row and col for the grid, that takes account for the fact that we want a min-x column
        int numRows = gameData.getDisplayHeight() / cellSize +1;
        int numCols = ((gameData.getDisplayWidth() - (2 * (gameData.getDisplayWidth() / 4))) / cellSize);

        //Center (X,Y) for screen & grid
        int centerScreenX = gameData.getDisplayWidth()/2;
        int centerScreenY = gameData.getDisplayHeight()/2;
        int centerGridX = (numCols/2) * cellSize;
        int centerGridY = (numRows/2) * cellSize;

        int startX = centerScreenX - centerGridX;
        int startY = centerScreenY - centerGridY;
        //We plus by 1, to get the start column to be a bit more towards x-positive. This way we get it into the center of the screen.
        int startCol = startX / cellSize + 1;
        int startRow = startY / cellSize +1;

        AsteroidNode[][] nodes = new AsteroidNode[numRows][numCols];

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                nodes[row][col] = new AsteroidNode(row + startRow, col + startCol);
            }
        }
        return nodes;
    }

    private void updateHitbox(Entity entity){
        Position pos = entity.getComponent(Position.class);
        Hitbox hitbox = entity.getComponent(Hitbox.class);
        hitbox.set(pos.getX(),pos.getY());
    }

    //Get random asteroid enum.
    public AsteroidSizes randomDirection() {
        AsteroidSizes[] asteroids = AsteroidSizes.class.getEnumConstants();
        return asteroids[getRandom.nextInt(asteroids.length)];
    }

}
