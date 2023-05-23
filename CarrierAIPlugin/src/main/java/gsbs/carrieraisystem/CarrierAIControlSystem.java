package gsbs.carrieraisystem;

import gsbs.common.components.*;
import gsbs.common.data.GameData;
import gsbs.common.data.Grid;
import gsbs.common.data.Node;
import gsbs.common.data.World;
import gsbs.common.data.enums.Teams;
import gsbs.common.entities.Carrier;
import gsbs.common.entities.Entity;
import gsbs.common.math.Distance;
import gsbs.common.services.IProcess;
import gsbs.common.util.ThetaStar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CarrierAIControlSystem implements IProcess {
    private final List<Entity> playerCarriers = new ArrayList<>();
    private final List<Entity> enemyCarriers = new ArrayList<>();

    public void process(GameData gameData, World world) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Future<List<Node>>> tasks = new ArrayList<>();
        List<Entity> sourceCarriers = new ArrayList<>();
        List<Entity> targetCarriers = new ArrayList<>();

        playerCarriers.clear();
        enemyCarriers.clear();

        // Get all carriers
        for (Entity carrier : world.getEntities(Carrier.class)) {
            var team = carrier.getComponent(Team.class);
            if (team.getTeam() == Teams.PLAYER) {
                playerCarriers.add(carrier);
                continue;
            }
            if (team.getTeam() == Teams.ENEMY) {
                enemyCarriers.add(carrier);
            }
        }

        // Do logic for each playerCarrier
        for (Entity playerCarrier : playerCarriers) {
            var targetCarrier = findTargetCarrier(playerCarrier, enemyCarriers);
            if (targetCarrier != null) {
                tasks.add(schedulePathfinding(gameData, executorService, world, playerCarrier, targetCarrier));
                sourceCarriers.add(playerCarrier);
                targetCarriers.add(targetCarrier);
            }
        }

        // Do logic for each enemyCarrier
        for (Entity enemyCarrier : enemyCarriers) {
            var targetCarrier = findTargetCarrier(enemyCarrier, playerCarriers);
            if (targetCarrier != null) {
                tasks.add(schedulePathfinding(gameData, executorService, world, enemyCarrier, targetCarrier));
                sourceCarriers.add(enemyCarrier);
                targetCarriers.add(targetCarrier);
            }
        }

        try {
            for (var i = 0; i < tasks.size(); i++) {
                List<Node> path = tasks.get(i).get();
                handleFoundPath(gameData, path, gameData.getGrid(), sourceCarriers.get(i));
                handleOffensiveAction(gameData, world, gameData.getGrid(), sourceCarriers.get(i), targetCarriers.get(i));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to wait for pathfinding task to complete");
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        executorService.shutdown();
    }

    private Entity findTargetCarrier(Entity carrier, List<Entity> opponentCarriers) {
        var carrierPosition = carrier.getComponent(Position.class);
        float carrierX = carrierPosition.getX();
        float carrierY = carrierPosition.getY();
        Entity targetCarrier = null;
        float distanceFromTarget = Float.MAX_VALUE;
        for (Entity opponentCarrier : opponentCarriers) {
            var opponentCarrierPosition = opponentCarrier.getComponent(Position.class);
            float opponentCarrierX = opponentCarrierPosition.getX();
            float opponentCarrierY = opponentCarrierPosition.getY();
            float newDistanceFromTarget = Distance.euclideanDistance(carrierX, carrierY, opponentCarrierX, opponentCarrierY);
            if (distanceFromTarget > newDistanceFromTarget) {
                distanceFromTarget = newDistanceFromTarget;
                targetCarrier = opponentCarrier;
            }
        }

        return targetCarrier;
    }

    private void handleOffensiveAction(GameData gameData, World world, Grid grid, Entity thisCarrier, Entity targetCarrier) {
        var positionAIShip = thisCarrier.getComponent(Position.class);
        var positionTarget = targetCarrier.getComponent(Position.class);

        float euclideanDistance = Distance.euclideanDistance(positionAIShip.getX(), positionAIShip.getY(), positionTarget.getX(), positionTarget.getY());

        if (euclideanDistance > 500)
            return;

        double directionDifference = getDirectionDifference(positionAIShip.getX(), positionAIShip.getY(), positionTarget.getX(), positionTarget.getY(), positionAIShip.getRadians());

        Weapon weapon = thisCarrier.getComponent(Weapon.class);
        // FIX! change weapon correctly
        weapon.changeWeapon();
        // shoot pistol when facing player, shoot shotgun when at a larger angle
        if (directionDifference > 2 * Math.PI - 0.3 || directionDifference < 0.3) {
            // Pistol
            weapon.changeWeapon();
            weapon.fire(thisCarrier, gameData, world);
        } else {
            // Shotgun
            weapon.changeWeapon();
            weapon.fire(thisCarrier, gameData, world);
        }
    }

    private Future<List<Node>> schedulePathfinding(GameData gameData, ExecutorService executorService, World world, Entity thisCarrier, Entity targetCarrier) {
        Grid grid = gameData.getGrid();
        var positionAIShip = thisCarrier.getComponent(Position.class);
        var positionTarget = targetCarrier.getComponent(Position.class);

        Node start = grid.getNodeFromCoords((int) positionAIShip.getX(), (int) positionAIShip.getY());
        Node goal = grid.getNodeFromCoords((int) positionTarget.getX(), (int) positionTarget.getY());
        var movementAIShip = thisCarrier.getComponent(Movement.class);

        movementAIShip.setLeft(false);
        movementAIShip.setRight(false);
        movementAIShip.setUp(true);


        return executorService.submit(() -> {
            ThetaStar thetaStar = new ThetaStar();
            var path = thetaStar.findPath(start, goal, grid);
            return Objects.requireNonNullElseGet(path, () -> List.of(goal, start));
        });
    }


    private void handleFoundPath(GameData gameData, List<Node> foundPath, Grid grid, Entity thisCarrier) {
        var positionAIShip = thisCarrier.getComponent(Position.class);
        var movementAIShip = thisCarrier.getComponent(Movement.class);

        // For debug (Assumed)
        gameData.setPath(thisCarrier, foundPath);
        // gameData.setPath(path);

        // Handle turning towards target node
        if (foundPath.size() > 1) {
            int[] desiredLocation = grid.getCoordsFromNode(foundPath.get(foundPath.size() - 2));
//            if (grid.getNodeFromCoords(desiredLocation[0], desiredLocation[1]).isBlocked()) {
//                System.out.println("THIS NODE IS BLOCKED: " + grid.getNodeFromCoords(desiredLocation[0], desiredLocation[1]));
//            }
            double directionDifferenceNode1 = getDirectionDifference(positionAIShip.getX(), positionAIShip.getY(), desiredLocation[0], desiredLocation[1], positionAIShip.getRadians());

            if (directionDifferenceNode1 > Math.PI) {
                movementAIShip.setLeft(true);
            } else {
                movementAIShip.setRight(true);
            }
        }

        // Handle Tokyo Drift error (drifting into asteroids when turning after reaching target node)
        if (foundPath.size() > 2) {
            int[] desiredLocation = grid.getCoordsFromNode(foundPath.get(foundPath.size() - 2));
            int[] NextDesiredLocation = grid.getCoordsFromNode(foundPath.get(foundPath.size() - 3));
            double directionDifferenceNode2 = getDirectionDifference(positionAIShip.getX(), positionAIShip.getY(), NextDesiredLocation[0], NextDesiredLocation[1], positionAIShip.getRadians());

            float distanceFromDesiredLocation = Distance.euclideanDistance(positionAIShip.getX(), positionAIShip.getY(), desiredLocation[0], desiredLocation[1]);
            // Check all conditions that cause a need to slow down the AI ship

            if (distanceFromDesiredLocation < 300 && movementAIShip.getVelocity() > 20 && (directionDifferenceNode2 < 2 * Math.PI - 0.3 || directionDifferenceNode2 > 0.3)) {
                movementAIShip.setUp(false);
                /*
                System.out.println("********************");
                System.out.println("SLOW DOWN!");
                System.out.println(distanceFromDesiredLocation);
                System.out.println(movementAIShip.getVelocity());
                System.out.println(directionDifferenceNode2); */
            }
        }

    }

    private double getDirectionDifference(float x1, float y1, float x2, float y2, float radians) {
        double desiredAngle = convertToUnitCircle(getDirection(x1, y1, x2, y2));
        double currentUnit = convertToUnitCircle(radians);
        return ((2 * Math.PI - currentUnit) + desiredAngle) % (2 * Math.PI);
    }

    private double getDirection(float x1, float y1, float x2, float y2) {
        return Math.atan2(y2 - y1, x2 - x1);

    }

    private double convertToUnitCircle(double dir) {
        return 2 * Math.PI - ((dir % (2 * Math.PI) + 2 * Math.PI) % (2 * Math.PI));
    }
}
