package gsbs.common.data;

import gsbs.common.components.Position;
import gsbs.common.entities.Entity;
import gsbs.common.entities.Flagship;

import java.util.ArrayList;
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
        this.grid = new Node[(maxRow*maxColumn)];
        for (int i = 0; i < maxRow; i++) {
            for (int j = 0; j < maxColumn; j++) {
                this.grid[i*j] = new Node(i, j, false);
            }
        }
    }
    
    
    public void updateGrid(World world){
        for (Entity entity : world.getEntities(Flagship.class)) {
            var position = entity.getComponent(Position.class);
            if (position != null) {
                getNodeFromCoords((int) position.getX(), (int) position.getY()).setBlocked(true);
            }
        }
    }

    public Node getNode(int row, int column){
        return this.grid[row * column];
    }

    public Node getNodeFromCoords(int x, int y){
        int row = maxRow / x;
        int column = maxColumn / y;
        return this.grid[row * column];
    }

    public Node[] getNeighbors(Node node){
        List<Node> nodes = new ArrayList<>();
        int nodeSpot = node.getRow() * node.getColumn();
        if (node.getRow() != 0){
            nodes.add(grid[nodeSpot - maxRow]);
        }
        if (node.getRow() != maxRow){
            nodes.add(grid[nodeSpot + maxRow]);
        }
        if (node.getColumn() != 0){
            nodes.add(grid[nodeSpot - 1]);
        }
        if (node.getRow() != maxColumn){
            nodes.add(grid[nodeSpot + 1]);
        }

        return nodes.toArray(new Node[0]);
    }

}


