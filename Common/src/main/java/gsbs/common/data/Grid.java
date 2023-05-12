package gsbs.common.data;

import gsbs.common.components.Position;
import gsbs.common.entities.Asteroid;
import gsbs.common.entities.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Grid {
    int nodeSize;
    int maxRow;
    int maxColumn;
    Node[] grid;


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

    public void updateGrid(World world){
        for (Entity asteroid : world.getEntities(Asteroid.class)) {
            var position = asteroid.getComponent(Position.class);
            if (position != null) {
                getNodeFromCoords((int) position.getX(), (int) position.getY()).setBlocked(true);
            }
        }

        for (Node node : grid){
            System.out.println(node.isBlocked());
        }
    }

    public Node getNode(int row, int column){
        return this.grid[row * column];
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
        result[0] = node.getRow() * nodeSize - (nodeSize/2);
        result[1] = node.getColumn() * nodeSize - (nodeSize/2);
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


