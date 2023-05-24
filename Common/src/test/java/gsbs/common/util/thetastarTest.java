package gsbs.common.util;

import gsbs.common.components.Position;
import gsbs.common.components.Sprite;
import gsbs.common.data.GameData;
import gsbs.common.data.Grid;
import gsbs.common.data.Node;
import gsbs.common.entities.Entity;
import gsbs.common.math.Distance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class thetastarTest {

    private GameData gameData;
    private Node[] grid;
    private ThetaStar thetaStar;
    private Node start;
    private Node goal;
    private Grid gridInstance;

    @BeforeEach
    void setUp(){
        int width = 1200;
        int height = 800;
        gridInstance = new Grid(10, width, height);
        grid = gridInstance.getGrid();

        start = gridInstance.getNodeFromCoords(10, 300);
        goal = gridInstance.getNodeFromCoords(1000, 300);
        thetaStar = new ThetaStar();
        blockNodes(120);
    }

    @Test
    void testPath(){
        List<Node> path = thetaStar.findPath(start,goal,gridInstance);
        List<Node> actualShortestPath = new ArrayList<>();
        actualShortestPath.add(gridInstance.getNode(100,30));
        actualShortestPath.add(gridInstance.getNode(18,42));
        actualShortestPath.add(gridInstance.getNode(12,42));
        actualShortestPath.add(gridInstance.getNode(9,41));
        actualShortestPath.add(gridInstance.getNode(4,37));
        actualShortestPath.add(gridInstance.getNode(1,30));


        System.out.println(path);
        assertTrue(path.equals(actualShortestPath));
    }

    private void blockNodes(int radius) {
         for (int i = 0; i < 16; i++) {
                for (Node node : grid) {
                    int[] nodeCoords = gridInstance.getCoordsFromNode(node);
                    float nodeCenterX = nodeCoords[0];
                    float nodeCenterY = nodeCoords[1];

                    // Calculate the distance between the node center and the entity's center
                    float distance = Distance.euclideanDistance(nodeCenterX, nodeCenterY, 10*i, 20*i);

                    if (distance <= radius) {
                        node.setBlocked(true);
                    }
                }
            }
        }
}

