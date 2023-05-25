package gsbs.common.math;

import gsbs.common.data.Node;

public class Distance {

    public static double euclideanNodeDistance(Node node, Node goal) {
        int deltaRow = node.getRow() - goal.getRow();
        int deltaColumn = node.getColumn() - goal.getColumn();
        return Math.sqrt(deltaRow * deltaRow + deltaColumn * deltaColumn);
    }

    public static float euclideanDistance(float x1, float y1, float x2, float y2) {
        float deltaX = x2 - x1;
        float deltaY = y2 - y1;
        return (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }
}
