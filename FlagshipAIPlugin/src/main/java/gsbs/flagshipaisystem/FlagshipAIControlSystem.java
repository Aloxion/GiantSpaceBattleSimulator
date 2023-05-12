package gsbs.flagshipaisystem;

import java.util.*;

import gsbs.common.components.Movement;
import gsbs.common.components.Position;
import gsbs.common.components.Team;
import gsbs.common.components.Weapon;
import gsbs.common.data.GameData;
import gsbs.common.data.Grid;
import gsbs.common.data.Node;
import gsbs.common.data.World;
import gsbs.common.data.enums.Teams;
import gsbs.common.entities.Entity;
import gsbs.common.entities.Flagship;

import gsbs.common.services.IProcess;
import java.util.List;

import static java.lang.Math.atan;

public class FlagshipAIControlSystem implements IProcess {
    private Entity thisFlagship;
    private Entity targetFlagship;
    private static Map<Node, Double> gScore = new HashMap<>();
    private static Map<Node, Node> parent = new HashMap<>();

    private Grid grid;




    public void process(GameData gameData, World world) {
        grid = gameData.getGrid();

        for (Entity flagship : world.getEntities(Flagship.class)) {
            var team = flagship.getComponent(Team.class);
            if (team.getTeam() == Teams.ENEMY) {
                thisFlagship = flagship;
                continue;
            }
            if (team.getTeam() == Teams.PLAYER) {
                targetFlagship = flagship;
            }
        }
        handlePathfinding(gameData, world, thisFlagship, targetFlagship);

    }

    private void handleOffensiveAction(GameData gameData, World world){
        Weapon weapon = thisFlagship.getComponent(Weapon.class);
        weapon.fire(thisFlagship, gameData, world);
    }

    private void handlePathfinding(GameData gameData, World world, Entity thisFlagship, Entity targetFlagship){
        var positionAIShip = thisFlagship.getComponent(Position.class);
        var positionTarget = targetFlagship.getComponent(Position.class);

        Node start = grid.getNodeFromCoords((int) positionAIShip.getX(), (int) positionAIShip.getY());
        Node goal = grid.getNodeFromCoords((int) positionTarget.getX(), (int) positionTarget.getY());

        var movementAIShip = thisFlagship.getComponent(Movement.class);
        if(heuristic(start, goal) > 2){
            movementAIShip.setUp(true);
        }
        else{
            handleOffensiveAction(gameData, world);
            movementAIShip.setUp(false);
        }

        movementAIShip.setLeft(false);
        movementAIShip.setRight(false);

        List<Node> thetaStarList = thetaStar(start, goal);
        int[] desiredLocation = grid.getCoordsFromNode(thetaStarList.get(thetaStarList.size()-2));
        gameData.setTarget(desiredLocation[0], desiredLocation[1]);

        double desiredAngle = convertToUnitCircle(getDirection(positionAIShip.getX(), positionAIShip.getY(), desiredLocation[0], desiredLocation[1]));
        double currUnit = convertToUnitCircle(positionAIShip.getRadians());
        double dirDiff = ((2*Math.PI - currUnit) + desiredAngle) % (2*Math.PI);

        if(dirDiff > Math.PI){
            movementAIShip.setLeft(true);
        }
        else {
            movementAIShip.setRight(true);
        }

    }

    private double getDirection(float x1, float y1, float x2, float y2){
        // System.out.println((float) Math.atan2(x2 - x1, y2 - y1));
        return Math.atan2(y2 - y1, x2 - x1);

    }

    private double convertToUnitCircle(double dir){
        return 2*Math.PI - ((dir % (2 * Math.PI) + 2 * Math.PI) % (2 * Math.PI));
    }

    public List<Node> thetaStar(Node start, Node goal) {
        gScore.put(start, 0.0);
        parent.put(start, start);

        Map<Node, Double> open = new HashMap();
        //Comparator.comparingDouble(node -> gScore.get(node) + heuristic(node, goal))
        open.put(start, gScore.get(start) + heuristic(start, goal));
        Set<Node> closed = new HashSet<>();

        while (!open.isEmpty()) {
            Node currentNode = getKeyByValue(open, Collections.min(open.values()));
            open.remove(currentNode);

            if (currentNode.equals(goal)) {
                return reconstructPath(currentNode);
            }
            closed.add(currentNode);

            for (Node neighbor : grid.getNeighbors(currentNode)) {
                if (!closed.contains(neighbor)) {
                    if (!open.containsKey(neighbor)) {
                        gScore.put(neighbor, Double.POSITIVE_INFINITY);
                        parent.put(neighbor, null);
                    }
                    updateVertex(currentNode, neighbor, open, goal);
                }
            }
        }

        return null;
    }

    private  void updateVertex(Node currentNode, Node neighbor, Map<Node, Double> open, Node goal) {
        if (lineOfSight(parent.get(currentNode), neighbor)) {
            if (gScore.get(parent.get(currentNode)) + cost(parent.get(currentNode), neighbor) < gScore.get(neighbor)) {
                gScore.put(neighbor, gScore.get(parent.get(currentNode)) + cost(parent.get(currentNode), neighbor));
                parent.put(neighbor, parent.get(currentNode));

                if (open.containsKey(neighbor)){
                    open.remove(neighbor);
                }

                open.put(neighbor, (gScore.get(neighbor) + heuristic(neighbor, goal)));
            }
        } else {
            if (gScore.get(currentNode) + cost(currentNode, neighbor) < gScore.get(neighbor)) {
                gScore.put(neighbor, gScore.get(currentNode) + cost(currentNode, neighbor));
                parent.put(neighbor, currentNode);

                if (open.containsKey(neighbor)){
                    open.remove(neighbor);
                }
                open.put(neighbor, (gScore.get(neighbor) + heuristic(neighbor, goal)));
            }
        }
    }

    private static List<Node> reconstructPath(Node currentNode) {
        List<Node> totalPath = new ArrayList<>();
        totalPath.add(currentNode);

        for (Object key : parent.keySet()){
            System.out.println(key);
        }

        if (!parent.get(currentNode).equals(currentNode)) {
            totalPath.addAll(reconstructPath(parent.get(currentNode)));
        }

        return totalPath;
    }

    private boolean lineOfSight(Node parent, Node neighbor){
        int x0 = parent.getRow();
        int y0 = parent.getColumn();
        int x1 = neighbor.getRow();
        int y1 = neighbor.getColumn();

        int dx = x1 - x0;
        int dy = y1 - y0;

        int f = 0;
        int sy = 1;
        int sx = 1;
        int offsetX = 0;
        int offsetY = 0;

        if (dy < 0) {
            dy *= -1;
            sy = -1;
            offsetY = -1;
        }
        if (dx < 0){
            dx *= -1;
            sx = -1;
            offsetX = -1;
        }

        if (dx >= dy){
            while (x0 != x1){
                f += dy;
                if (f >= dx){
                    if (grid.getNode(x0 + offsetX,y0 + offsetY).isBlocked()) {
                        return false;
                    }
                    y0 += sy;
                    f -= dx;
                }
                if (f != 0 && grid.getNode(x0 + offsetX,y0 + offsetY).isBlocked()){
                    return false;
                }
                if (dy == 0 && grid.getNode(x0 + offsetX,y0).isBlocked() && grid.getNode(x0 + offsetX,y0-1).isBlocked()){
                    return false;
                }
                x0 += sx;
            }
        } else {
            while (y0 != y1){
                f += dx;
                if (f >= dy){
                    if (grid.getNode(x0 + offsetX,y0 + offsetY).isBlocked()) {
                        return false;
                    }
                    x0 += sx;
                    f -= dy;
                }
                if (f != 0 && grid.getNode(x0 + offsetX,y0 + offsetY).isBlocked()){
                    return false;
                }
                if (dx == 0 && grid.getNode(x0,y0 + offsetY).isBlocked() && grid.getNode(x0-1,y0 + offsetY).isBlocked()) {
                    return false;
                }
                y0 += sy;
            }
        }
        return true;
    }

//    private boolean lineOfSight(Node parent, Node neighbor) {
//        // Get the row and column coordinates of the parent and neighbor nodes
//        int x0 = parent.getRow();
//        int y0 = parent.getColumn();
//        int x1 = neighbor.getRow();
//        int y1 = neighbor.getColumn();
//
//        // Calculate the differences in the row and column coordinates
//        int dx = Math.abs(x1 - x0);
//        int dy = Math.abs(y1 - y0);
//
//        // Determine the direction of the line of sight
//        int sx = x0 < x1 ? 1 : -1;
//        int sy = y0 < y1 ? 1 : -1;
//
//        // Initialize the error term for Bresenham's line algorithm
//        int err = dx - dy;
//
//        // Continue until the current node reaches the neighbor node
//        while (true) {
//            // Get the current node at the current row and column coordinates
//            Node currentNode = grid.getNode(x0, y0);
//
//            // Check if the current node is blocked
//            if (currentNode.isBlocked()) {
//                System.out.println("Blocked node: (" + x0 + ", " + y0 + ")");
//                return false;
//            }
//
//            // If the current node has reached the neighbor node, break the loop
//            if (x0 == x1 && y0 == y1) {
//                break;
//            }
//
//            // Calculate the next error term for Bresenham's line algorithm
//            int e2 = 2 * err;
//
//            // Update the row coordinate if needed
//            if (e2 > -dy) {
//                err -= dy;
//                x0 += sx;
//            }
//
//            // Update the column coordinate if needed
//            if (e2 < dx) {
//                err += dx;
//                y0 += sy;
//            }
//        }
//
//        // If the loop has completed, it means there is a line of sight
//        return true;
//    }

    private static double cost(Node currentNode, Node neighbor) {
        // Euclidean distance cost function
        int deltaRow = currentNode.getRow() - neighbor.getRow();
        int deltaColumn = currentNode.getColumn() - neighbor.getColumn();
        return Math.sqrt(deltaRow * deltaRow + deltaColumn * deltaColumn);
    }

    private static double heuristic(Node node, Node goal) {
        // Euclidean distance heuristic
        int deltaRow = node.getRow() - goal.getRow();
        int deltaColumn = node.getColumn() - goal.getColumn();
        return Math.sqrt(deltaRow * deltaRow + deltaColumn * deltaColumn);
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
