package gsbs.carrierai;

import gsbs.common.components.Health;
import gsbs.common.components.Position;
import gsbs.common.components.Team;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.entities.Carrier;
import gsbs.common.entities.Entity;
import gsbs.common.math.Vector2;
import gsbs.common.services.IProcess;

public class CarrierAIProcessor implements IProcess {
    @Override
    public void process(GameData gameData, World world) {
        // Control
        // Find nearest enemy ship
        // Thetastar route
        // Shoot when close enough

        for (var entity : world.getEntities(Carrier.class)) {
            Vector2 carrierPosition = entity.getComponent(Position.class).asVector();
            Entity closestShip = null;
            float closestShipDistance = Float.MAX_VALUE;

            for (var other : world.getEntitiesWithComponents(Team.class, Health.class, Position.class)) {
                var otherTeam = other.getComponent(Team.class);

                if (!otherTeam.isInSameTeam(entity)) {
                    var distance = other.getComponent(Position.class).asVector().subtract(carrierPosition).length();

                    if (distance < closestShipDistance) {
                        closestShipDistance = distance;
                        closestShip = other;
                    }
                }
            }

            if (closestShip == null)
                continue;

            // Check if we need to turn left or right
            var closestShipPosition = closestShip.getComponent(Position.class).asVector();
            var currentDir = normalizeAngle(entity.getComponent(Position.class).getRadians());
            var targetDir = closestShipPosition.subtract(carrierPosition).direction();
            entity.getComponent(Position.class).setRadians(targetDir);
//
//            var difference = currentDir - targetDir;
//
//            if (difference >= 0) {
//                entity.getComponent(Movement.class).setRight(true);
//                entity.getComponent(Movement.class).setLeft(false);
//            } else if (difference < 0) {
//                entity.getComponent(Movement.class).setRight(false);
//                entity.getComponent(Movement.class).setLeft(true);
//            }

//            entity.getComponent(Movement.class).setUp(true);
        }
    }

//    private void handlePathfinding(GameData gameData, World world, Entity thisFlagship, Entity targetFlagship) {
//        Grid grid = gameData.getGrid();
//        var positionAIShip = thisFlagship.getComponent(Position.class);
//        var positionTarget = targetFlagship.getComponent(Position.class);
//        var spriteAIShip = thisFlagship.getComponent(Sprite.class);
//        var spriteTarget = targetFlagship.getComponent(Sprite.class);
//        float euclideanDistance = Distance.euclideanDistance(positionAIShip.getX(), positionAIShip.getY(), positionTarget.getX(), positionTarget.getY());
//
//        Node start = grid.getNodeFromCoords((int) positionAIShip.getX() + spriteAIShip.getWidth() / 2, (int) positionAIShip.getY() + spriteAIShip.getHeight() / 2);
//        Node goal = grid.getNodeFromCoords((int) positionTarget.getX() + spriteTarget.getWidth() / 2, (int) positionTarget.getY() + spriteTarget.getHeight() / 2);
//        var movementAIShip = thisFlagship.getComponent(Movement.class);
//
//        movementAIShip.setLeft(false);
//        movementAIShip.setRight(false);
//
//
//        ThetaStar thetaStar = new ThetaStar();
//        List<Node> newPath = thetaStar.findPath(start, goal, grid);
//        if (newPath != null) {
//            path = newPath;
//        } else {
//            newPath = new ArrayList<>();
//            newPath.add(goal);
//            newPath.add(start);
//            path = newPath;
//        }
//
//        // For debug (Assumed)
//        gameData.setPath(path);
////        System.out.println("Path: " + path);
//
//        // Handle turning towards target node
//        if (path.size() > 1) {
//            int[] desiredLocation = grid.getCoordsFromNode(path.get(path.size() - 2));
//            if (grid.getNodeFromCoords(desiredLocation[0], desiredLocation[1]).isBlocked()) {
//                System.out.println("THIS NODE IS BLOCKED: " + grid.getNodeFromCoords(desiredLocation[0], desiredLocation[1]));
//            }
//            double directionDifferenceNode1 = getDirectionDifference(positionAIShip.getX(), positionAIShip.getY(), desiredLocation[0], desiredLocation[1], positionAIShip.getRadians());
//
//            if (directionDifferenceNode1 > Math.PI) {
//                movementAIShip.setLeft(true);
//            } else {
//                movementAIShip.setRight(true);
//            }
//
//            if (euclideanDistance < 500) {
//                handleOffensiveAction(gameData, world, directionDifferenceNode1);
//            }
//        }
//
//        // Handle Tokyo Drift error (drifting into asteroids when turning after reaching target node)
//        if (path.size() > 2) {
//            int[] desiredLocation = grid.getCoordsFromNode(path.get(path.size() - 2));
//            int[] NextDesiredLocation = grid.getCoordsFromNode(path.get(path.size() - 3));
//            double directionDifferenceNode2 = getDirectionDifference(positionAIShip.getX(), positionAIShip.getY(), NextDesiredLocation[0], NextDesiredLocation[1], positionAIShip.getRadians());
//
//            float distanceFromDesiredLocation = Distance.euclideanDistance(positionAIShip.getX(), positionAIShip.getY(), desiredLocation[0], desiredLocation[1]);
//            // Check all conditions that cause a need to slow down the AI ship
//            // System.out.println(movementAIShip.getVelocity());
//            if (distanceFromDesiredLocation < 200 && movementAIShip.getVelocity() > 30 && (directionDifferenceNode2 < 2 * Math.PI - 0.3 || directionDifferenceNode2 > 0.3)) {
//                movementAIShip.setUp(false);
////                System.out.println("********************");
////                System.out.println("SLOW DOWN!");
////                System.out.println(distanceFromDesiredLocation);
////                System.out.println(movementAIShip.getVelocity());
////                System.out.println(directionDifferenceNode2);
//            }
//        }
//        movementAIShip.setUp(true);
//    }

    private float normalizeAngle(float angle) {
        return (float) ((angle % (2 * Math.PI) + (2 * Math.PI)) % (2 * Math.PI));
    }
}
