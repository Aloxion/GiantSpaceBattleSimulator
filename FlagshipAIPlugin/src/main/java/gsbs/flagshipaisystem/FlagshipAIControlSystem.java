package gsbs.flagshipaisystem;

import java.util.*;

import gsbs.common.components.Movement;
import gsbs.common.components.Position;
import gsbs.common.components.Team;
import gsbs.common.data.GameData;
import gsbs.common.data.Grid;
import gsbs.common.data.Node;
import gsbs.common.data.World;
import gsbs.common.data.enums.Teams;
import gsbs.common.entities.Entity;
import gsbs.common.entities.Flagship;

import gsbs.common.services.IProcess;
import java.util.List;

public class FlagshipAIControlSystem implements IProcess {
    private Entity thisFlagship;
    private Entity targetFlagship;
    private static Map<Node, Double> gScore = new HashMap<>();
    private static Map<Node, Node> parent = new HashMap<>();

    private Grid grid;




    public void process(GameData gameData, World world) {
        grid = gameData.getGrid();
        System.out.println("AI control system go YEEEEE");

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
    }

    private void handlePathfinding(GameData gameData, World world, Entity thisFlagship, Entity targetFlagship){
        var positionAIShip = thisFlagship.getComponent(Position.class);
        var positionTarget = targetFlagship.getComponent(Position.class);

        Node start = grid.getNodeFromCoords((int) positionAIShip.getX(), (int) positionAIShip.getY());
        Node goal = grid.getNodeFromCoords((int) positionTarget.getX(), (int) positionTarget.getY());
        thetaStar(start, goal);

        var movementAIShip = thisFlagship.getComponent(Movement.class);
        if(heuristic(start, goal) < 50){
            handleOffensiveAction(gameData, world);
            movementAIShip.setUp(false);
        }
        else{
            movementAIShip.setUp(true);
        }

        positionAIShip.setRadians(getDirection(positionAIShip.getX(), positionAIShip.getY(), positionTarget.getX(), positionTarget.getY()));

    }

    private float getDirection(float x1, float y1, float x2, float y2){
        return (float) Math.atan2(x2 - x1, y2 - y1);

    }

    public List<Node> thetaStar(Node start, Node goal) {
        gScore.put(start, 0.0);
        parent.put(start, start);

        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(node -> gScore.get(node) + heuristic(node, goal)));
        open.add(start);

        Set<Node> closed = new HashSet<>();

        while (!open.isEmpty()) {
            Node currentNode = open.poll();
            if (currentNode.equals(goal)) {
                return reconstructPath(currentNode);
            }

            closed.add(currentNode);

            for (Node neighbor : grid.getNeighbors(currentNode)) {
                if (!closed.contains(neighbor)) {
                    if (!open.contains(neighbor)) {
                        gScore.put(neighbor, Double.POSITIVE_INFINITY);
                        parent.put(neighbor, null);
                    }
                    updateVertex(currentNode, neighbor, open);
                }
            }
        }
        return null;
    }


    private  void updateVertex(Node currentNode, Node neighbor, PriorityQueue<Node> open) {
        if (lineOfSight(parent.get(currentNode), neighbor)) {
            if (gScore.get(parent.get(currentNode)) + cost(parent.get(currentNode), neighbor) < gScore.get(neighbor)) {
                gScore.put(neighbor, gScore.get(parent.get(currentNode)) + cost(parent.get(currentNode), neighbor));
                parent.put(neighbor, parent.get(currentNode));

                open.remove(neighbor);
                open.add(neighbor);
            }
        } else {
            if (gScore.get(currentNode) + cost(currentNode, neighbor) < gScore.get(neighbor)) {
                gScore.put(neighbor, gScore.get(currentNode) + cost(currentNode, neighbor));
                parent.put(neighbor, currentNode);

                open.remove(neighbor);
                open.add(neighbor);
            }
        }
    }

    private static List<Node> reconstructPath(Node currentNode) {
        List<Node> totalPath = new ArrayList<>();
        totalPath.add(currentNode);

        if (!parent.get(currentNode).equals(currentNode)) {
            totalPath.addAll(reconstructPath(parent.get(currentNode)));
        }

        return totalPath;
    }

    private boolean lineOfSight(Node parent, Node neighbor) {
        // Get the row and column coordinates of the parent and neighbor nodes
        int x0 = parent.getRow();
        int y0 = parent.getColumn();
        int x1 = neighbor.getRow();
        int y1 = neighbor.getColumn();

        // Calculate the differences in the row and column coordinates
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        // Determine the direction of the line of sight
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;

        // Initialize the error term for Bresenham's line algorithm
        int err = dx - dy;

        // Continue until the current node reaches the neighbor node
        while (true) {
            // Get the current node at the current row and column coordinates
            Node currentNode =  grid.getNode(x0, y0);

            // Check if the current node is blocked
            if (currentNode.isBlocked()) {
                return false;
            }

            // If the current node has reached the neighbor node, break the loop
            if (x0 == x1 && y0 == y1) {
                break;
            }

            // Calculate the next error term for Bresenham's line algorithm
            int e2 = 2 * err;

            // Update the row coordinate if needed
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }

            // Update the column coordinate if needed
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }

        // If the loop has completed, it means there is a line of sight
        return true;
    }

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
}
