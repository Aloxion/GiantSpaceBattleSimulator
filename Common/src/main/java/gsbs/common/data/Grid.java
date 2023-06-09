package gsbs.common.data;

import gsbs.common.components.Position;
import gsbs.common.components.Sprite;
import gsbs.common.entities.Asteroid;
import gsbs.common.entities.Entity;
import gsbs.common.math.Distance;
import gsbs.common.math.Vector2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Grid {
    int nodeSize;
    int maxColumn;
    int maxRow;
    Node[] grid;
    boolean printGrid = false;
    boolean updateGridFlag = true;
    float steepness = 2;
    float tolerance = 20000;


    public Grid(int nodeSize, int displayWidth, int displayHeight) {
        this.nodeSize = nodeSize;
        this.maxColumn = displayWidth / nodeSize;
        this.maxRow = displayHeight / nodeSize;
        this.grid = new Node[maxColumn * maxRow];
        int index = 0;
        for (int i = 0; i < maxColumn; i++) {
            index = i * maxRow - 1;
            for (int j = 0; j < maxRow; j++) {
                index += 1;
                this.grid[index] = new Node(i, j, false, false);
            }
        }
    }

    public int getNodeSize() {
        return nodeSize;
    }

    public void updateGrid(World world) {
        if (updateGridFlag) {
            List<Entity> entitiesToBlock = new ArrayList<>();
            entitiesToBlock.addAll(world.getEntities(Asteroid.class));
            blockNodesFromEntities(entitiesToBlock.toArray(new Entity[0]));
            setNodeCollisions(entitiesToBlock.toArray(new Entity[0]));
            addWeightsToNodes(entitiesToBlock.toArray(new Entity[0]));

            // Block the rim and make it collidable
            for (int i = 0; i < maxColumn; i++) {
                for (int j = 0; j < 2; j++) {
                    Node topNode = getNode(i, j);
                    Node bottomNode = getNode(i, maxRow - 1 - j);
                    topNode.setCollidable(true);
                    topNode.setBlocked(true);
                    topNode.setCollisionVector(new Vector2(0, 1f));

                    bottomNode.setCollidable(true);
                    bottomNode.setBlocked(true);
                    bottomNode.setCollisionVector(new Vector2(0, -1f));
                }
            }
            for (int i = 0; i < maxRow; i++) {
                for (int j = 0; j < 2; j++) {
                    Node leftNode = getNode(j, i);
                    Node rightNode = getNode(maxColumn - 1 - j, i);
                    leftNode.setCollidable(true);
                    leftNode.setBlocked(true);
                    leftNode.setCollisionVector(new Vector2(1, 0));
                    rightNode.setCollidable(true);
                    rightNode.setBlocked(true);
                    rightNode.setCollisionVector(new Vector2(-1, 0));
                }
            }
        }


        updateGridFlag = false;
        if (printGrid) {
            printGridWeights();
            printGrid();
        }
    }

    private void addWeightsToNodes(Entity[] blockingEntities) {
        float smallestDistanceFromBlockingEntity;
        for (Node node : grid) {
            if (node.isBlocked()) {
                node.setWeight(Float.MAX_VALUE);
                continue;
            }
            smallestDistanceFromBlockingEntity = Float.MAX_VALUE;
            int[] nodeCoords = getCoordsFromNode(node);
            for (Entity entity : blockingEntities) {
                var position = entity.getComponent(Position.class);
                var sprite = entity.getComponent(Sprite.class);

                if (position != null && sprite != null) {
                    float asteroidCenterX = position.getX();
                    float asteroidCenterY = position.getY();

                    float radius = sprite.getWidth() / 2;
                    float distance = Distance.euclideanDistance(nodeCoords[0], nodeCoords[1], asteroidCenterX, asteroidCenterY) - radius;

                    if (distance <= smallestDistanceFromBlockingEntity) {
                        smallestDistanceFromBlockingEntity = distance;
                    }
                }
            }
            float weight = (float) (tolerance / Math.pow(smallestDistanceFromBlockingEntity, steepness));
            if (weight > 999)
                weight = 999;
            node.setWeight(weight);
        }
    }

    private void blockNodesFromEntities(Entity[] blockingEntities) {
        for (Entity entity : blockingEntities) {
            var position = entity.getComponent(Position.class);
            var sprite = entity.getComponent(Sprite.class);

            if (position != null && sprite != null) {
                float asteroidCenterX = position.getX();
                float asteroidCenterY = position.getY();

                for (Node node : grid) {
                    int[] nodeCoords = getCoordsFromNode(node);
                    float nodeCenterX = nodeCoords[0];
                    float nodeCenterY = nodeCoords[1];

                    // Calculate the distance between the node center and the entity's center
                    float distance = Distance.euclideanDistance(nodeCenterX, nodeCenterY, asteroidCenterX, asteroidCenterY);

                    float radius = sprite.getWidth() / 2 + 10;

                    if (distance <= radius) {
                        node.setBlocked(true);
                    }
                }
            }
        }
    }

    private void setNodeCollisions(Entity[] collisionEntities) {
        for (Entity entity : collisionEntities) {
            var position = entity.getComponent(Position.class);
            var sprite = entity.getComponent(Sprite.class);

            if (position != null && sprite != null) {
                float asteroidCenterX = position.getX();
                float asteroidCenterY = position.getY();

                for (Node node : grid) {
                    int[] nodeCoords = getCoordsFromNode(node);
                    float nodeCenterX = nodeCoords[0];
                    float nodeCenterY = nodeCoords[1];

                    // Calculate the distance between the node center and the entity's center
                    float distance = Distance.euclideanDistance(nodeCenterX, nodeCenterY, asteroidCenterX, asteroidCenterY);

                    float radius = sprite.getWidth() / 2 + 15;

                    if (distance <= radius) {
                        float vectorX = nodeCoords[0] - asteroidCenterX;
                        float vectorY = nodeCoords[1] - asteroidCenterY;
                        node.setCollisionVector(new Vector2(vectorX, vectorY).normalize());
                        node.setCollidable(true);
                    }
                }
            }
        }
    }

    public void printGrid() {
        printGrid = true;
        for (int i = 0; i < maxColumn; i++) {
            for (int j = 0; j < maxRow; j++) {
                Node node = getNode(i, j);
                if (node.isBlocked()) {
                    System.out.print("[X]");  // Blocked node marker
                } else {
                    System.out.print("[ ]");  // Empty node marker
                }
            }
            System.out.println();  // Move to the next row
        }
    }

    public void printGridWeights() {
        printGrid = true;
        for (int j = 0; j < maxRow; j++) {
            for (int i = 0; i < maxColumn; i++) {
                Node node = getNode(i, j);
                if (node.isBlocked()) {
                    System.out.print("\033[31m[XXX]\033[0m");  // Blocked node marker in red
                } else {
                    String weightString = String.format("%3d", (int) node.getWeight());
                    System.out.print("\033[32m[" + weightString + "]\033[0m");  // Empty node marker in green
                }
            }
            System.out.println();  // Move to the next column
        }
    }

    public Node getNode(int row, int column) {
        return this.grid[row * maxRow + column];
    }

    public Node getNodeFromCoords(int x, int y) {
        // FIX! can hit out of bounds at the highest x and y value
        int row = x / nodeSize;
        int column = y / nodeSize;
        if (row > maxColumn - 1) {
            row = maxColumn - 1;
        }

        int index = row * maxRow + column;

        if (index < this.grid.length) {
            return this.grid[index];

        } else {
            return this.grid[this.grid.length - 1];
        }
    }

    public int[] getCoordsFromNode(Node node) {
        int[] result = new int[2];
        result[0] = node.getRow() * nodeSize + (nodeSize / 2);
        result[1] = node.getColumn() * nodeSize + (nodeSize / 2);
        return result;
    }

    public Node[] getNeighbors(Node node) {
        List<Node> nodes = new ArrayList<>();
        int nodeSpot = node.getRow() * maxRow + node.getColumn();
        if (node.getRow() != 0) {
            nodes.add(grid[nodeSpot - maxRow]);
        }
        if (node.getRow() != maxColumn - 1) {
            nodes.add(grid[nodeSpot + maxRow]);
        }
        if (node.getColumn() != 0) {
            nodes.add(grid[nodeSpot - 1]);
        }
        if (node.getColumn() != maxRow - 1) {
            nodes.add(grid[nodeSpot + 1]);
        }
        return nodes.toArray(new Node[0]);
    }

    public Node[] getGrid(){
        return grid;
    }
}


