package gsbs.common.data;

import gsbs.common.components.Position;
import gsbs.common.components.Sprite;
import gsbs.common.entities.Asteroid;
import gsbs.common.entities.Entity;
import gsbs.common.math.Distance;

import java.util.ArrayList;
import java.util.List;

public class Grid {
    int nodeSize;
    int maxRow;
    int maxColumn;
    Node[] grid;
    boolean printedGrid = false;
    boolean updateGridFlag = true;
    float steepness = 2;
    float tolerance = 20000;


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
        /* Debug for theta star
        for (Entity flagship : world.getEntities(Flagship.class)) {
            var team = flagship.getComponent(Team.class);
            if (team.getTeam() == Teams.ENEMY) {
                var positionAIShip = flagship.getComponent(Position.class);
                Node tempNode = getNodeFromCoords((int) positionAIShip.getX(), (int) positionAIShip.getY());
                tempNode.setBlocked(true);
                Node[] tempArray = getNeighbors(tempNode);
                for (Node node : tempArray){
                    node.setBlocked(true);
                }

            }
        }*/
        if (updateGridFlag) {
            List<Entity> entitiesToBlock = new ArrayList<>();
            entitiesToBlock.addAll(world.getEntities(Asteroid.class));
            blockNodesFromEntities(entitiesToBlock.toArray(new Entity[0]));
            addWeightsToNodes(entitiesToBlock.toArray(new Entity[0]));

            // Block the rim
            for (int i = 0; i < maxRow; i++) {
                getNode(i, 0).setBlocked(true);
                getNode(i, maxColumn - 1).setBlocked(true);
            }
            for (int i = 0; i < maxColumn; i++) {
                getNode(0, i).setBlocked(true);
                getNode(maxRow - 1, i).setBlocked(true);
            }
        }


        updateGridFlag = false;
        if (!printedGrid)
            printGridWeights();
    }

    private void addWeightsToNodes(Entity[] blockingEntities) {
        float smallestDistanceFromBlockingEntity;
        for (Node node : grid) {
            if (node.isBlocked()){
                node.setWeight(Float.MAX_VALUE);
                continue;
            }
            smallestDistanceFromBlockingEntity = Float.MAX_VALUE;
            int[] nodeCoords = getCoordsFromNode(node);
            for (Entity entity : blockingEntities) {
                var position = entity.getComponent(Position.class);
                var sprite = entity.getComponent(Sprite.class);

                if (position != null && sprite != null) {
                    float asteroidCenterX = position.getX() + sprite.getWidth() / 2;
                    float asteroidCenterY = position.getY() + sprite.getHeight() / 2;

                    float radius = sprite.getWidth() / 2;
                    float distance = Distance.euclideanDistance(nodeCoords[0], nodeCoords[1], asteroidCenterX, asteroidCenterY) - radius;;

                    if (distance <= smallestDistanceFromBlockingEntity) {
                        smallestDistanceFromBlockingEntity = distance;
                    }
                }
            }
            float weight = (float) (tolerance/Math.pow(smallestDistanceFromBlockingEntity, steepness));
            if (weight > 999)
                weight = 999;
            node.setWeight(weight);
        }
    }
    private void blockNodesFromEntities (Entity[] blockingEntities){
        for (Entity entity : blockingEntities) {
            var position = entity.getComponent(Position.class);
            var sprite = entity.getComponent(Sprite.class);

            if (position != null && sprite != null) {
                float asteroidCenterX = position.getX() + sprite.getWidth() / 2;
                float asteroidCenterY = position.getY() + sprite.getHeight() / 2;

                for (Node node : grid) {
                    int nodeX = getCoordsFromNode(node)[0];
                    int nodeY = getCoordsFromNode(node)[1];
                    float nodeCenterX = nodeX + nodeSize / 2;
                    float nodeCenterY = nodeY + nodeSize / 2;

                    // Calculate the distance between the node center and the entity's center
                    float distance = Distance.euclideanDistance(nodeCenterX, nodeCenterY, asteroidCenterX, asteroidCenterY);

                    float radius = sprite.getWidth() / 2 + 20;

                    if (distance <= radius) {
                        node.setBlocked(true);
                    }
                }
            }
        }
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
    public void printGridWeights() {
        printedGrid = true;
        for (int j = 0; j < maxColumn; j++) {
            for (int i = 0; i < maxRow; i++) {
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

    public Node getNode(int row, int column){
        return this.grid[row * maxColumn + column];
    }

    public Node getNodeFromCoords(int x, int y){
        // FIX! can hit out of bounds at the highest x and y value
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


