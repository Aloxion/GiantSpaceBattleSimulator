package gsbs.common.util;

import gsbs.common.data.Grid;
import gsbs.common.data.Node;

import java.util.*;

public class ThetaStar {
    private static Map<Node, Double> gScore = new HashMap<>();
    private static Map<Node, Node> parent = new HashMap<>();

    private Grid grid;

    public List<Node> findPath(Node start, Node goal, Grid grid) {
        if(!initPathCheck(start, goal)){
            return null;
        }
        this.grid = grid;
        gScore.put(start, 0.0);
        parent.put(start, start);

        Map<Node, Double> open = new HashMap();
        //Comparator.comparingDouble(node -> gScore.get(node) + heuristic(node, goal))
        open.put(start, gScore.get(start) + heuristic(start, goal));
        Set<Node> closed = new HashSet<>();

        while (!open.isEmpty()) {
            Node currentNode = getKeyByValue(open, Collections.min(open.values()));
            open.remove(getKeyByValue(open, Collections.min(open.values())));

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

    private boolean initPathCheck(Node start, Node goal){
        if(start.isBlocked()){
            return false;
        }
        else return true;
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
            if (gScore.get(currentNode) + cost(currentNode, neighbor) < gScore.get(neighbor) && !neighbor.isBlocked()) {
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
        int sy = 0;
        int sx = 0;

        if (dy < 0) {
            dy *= -1;
            sy = -1;
        } else {
            sy = 1;
        }
        if (dx < 0){
            dx *= -1;
            sx = -1;
        } else {
            sx = 1;
        }

        if (dx >= dy){
            while (x0 != x1){
                f += dy;
                if (f >= dx){
                    if (grid.getNode(x0 + ((sx-1)/2),y0 + ((sy-1)/2)).isBlocked()) {
                        return false;
                    }
                    y0 += sy;
                    f -= dx;
                }
                if (f != 0 && grid.getNode(x0 + ((sx-1)/2),y0 + ((sy-1)/2)).isBlocked()){
                    return false;
                }
                if (dy == 0 && grid.getNode(x0 + ((sx-1)/2),y0).isBlocked() && grid.getNode(x0 + ((sx-1)/2),y0-1).isBlocked()){
                    return false;
                }
                x0 += sx;
            }
        } else {
            while (y0 != y1){
                f += dx;
                if (f >= dy){
                    if (grid.getNode(x0 + ((sx-1)/2),y0 + ((sy-1)/2)).isBlocked()) {
                        return false;
                    }
                    x0 += sx;
                    f -= dy;
                }
                if (f != 0 && grid.getNode(x0 + ((sx-1)/2),y0 + ((sy-1)/2)).isBlocked()){
                    return false;
                }
                if (dx == 0 && grid.getNode(x0,y0 + ((sy-1)/2)).isBlocked() && grid.getNode(x0-1,y0 + ((sy-1)/2)).isBlocked()) {
                    return false;
                }
                y0 += sy;
            }
        }
        return true;
    }


    private static double cost(Node currentNode, Node neighbor) {
        // Euclidean distance cost function
        int deltaRow = currentNode.getRow() - neighbor.getRow();
        int deltaColumn = currentNode.getColumn() - neighbor.getColumn();
        // add " + neighbor.getWeight()" to make it weighted Theta Star
        return Math.sqrt(deltaRow * deltaRow + deltaColumn * deltaColumn);
    }

    private static double heuristic(Node node, Node goal) {
        // Euclidean distance heuristic
        int deltaRow = node.getRow() - goal.getRow();
        int deltaColumn = node.getColumn() - goal.getColumn();
        return Math.sqrt(deltaRow * deltaRow + deltaColumn * deltaColumn) + node.getWeight();
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
