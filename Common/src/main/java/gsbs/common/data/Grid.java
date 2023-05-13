package gsbs.common.data;

import gsbs.common.components.Position;
import gsbs.common.components.Sprite;
import gsbs.common.entities.Asteroid;
import gsbs.common.entities.Entity;

import java.util.ArrayList;
import java.util.List;

public class Grid {
    int nodeSize;
    int maxRow;
    int maxColumn;
    Node[] grid;
    boolean printedGrid = false;


    public Grid(int nodeSize, int displayWidth, int displayHeight) {
        this.nodeSize = nodeSize;
        this.maxRow = displayWidth/nodeSize;
        this.maxColumn = displayHeight/nodeSize;
        this.grid = new Node[maxRow*maxColumn];
        int index = 0;
        for (int i = 0; i < maxRow; i++) {
            index = i * maxColumn - 1;
            for (int j = 0; j < maxColumn; j++) {
                index += 1;
                this.grid[index] = new Node(i, j, false);
            }
        }
    }

    public void updateGrid(World world) {
        for (Entity asteroid : world.getEntities(Asteroid.class)) {
            var position = asteroid.getComponent(Position.class);
            var sprite = asteroid.getComponent(Sprite.class);

            if (position != null && sprite != null) {
                float asteroidCenterX = position.getX() + sprite.getWidth() / 2;
                float asteroidCenterY = position.getY() + sprite.getHeight() / 2;

                for (Node node : grid) {
                    int nodeX = getCoordsFromNode(node)[0];
                    int nodeY = getCoordsFromNode(node)[1];
                    float nodeCenterX = nodeX + nodeSize / 2;
                    float nodeCenterY = nodeY + nodeSize / 2;

                    // Calculate the distance between the node center and the asteroid's center
                    float distance = calculateDistance(nodeCenterX, nodeCenterY, asteroidCenterX, asteroidCenterY);

                    // Adjust the blocking radius based on the desired value
                    float radius = sprite.getWidth()/2 + 20f;

                    if (distance <= radius) {
                        node.setBlocked(true);
                    }
                }
            }
        }
    }

    // Helper method to calculate the distance between two points
    private float calculateDistance(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public void printGrid() {
        printedGrid = true;
        for (int i = 0; i < maxRow; i++) {
            for (int j = 0; j < maxColumn; j++) {
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

    public Node getNode(int row, int column){
        return this.grid[row * maxColumn + column];
    }

    public Node getNodeFromCoords(int x, int y){
        int row = x / nodeSize;
        int column = y / nodeSize;
        if (row > maxRow-1){
            row = maxRow-1;
        }
        return this.grid[row * maxColumn + column];
    }

    public int[] getCoordsFromNode(Node node){
        int[] result = new int[2];
        result[0] = node.getRow() * nodeSize + (nodeSize/2);
        result[1] = node.getColumn() * nodeSize + (nodeSize/2);
        return result;
    }

    public Node[] getNeighbors(Node node){
        List<Node> nodes = new ArrayList<>();
        int nodeSpot = node.getRow() * maxColumn + node.getColumn();
        if (node.getRow() != 0){
            nodes.add(grid[nodeSpot - maxColumn]);
        }
        if (node.getRow() != maxRow-1){
            nodes.add(grid[nodeSpot + maxColumn]);
        }
        if (node.getColumn() != 0){
            nodes.add(grid[nodeSpot - 1]);
        }
        if (node.getColumn() != maxColumn-1){
            nodes.add(grid[nodeSpot + 1]);
        }
        return nodes.toArray(new Node[0]);
    }

}


