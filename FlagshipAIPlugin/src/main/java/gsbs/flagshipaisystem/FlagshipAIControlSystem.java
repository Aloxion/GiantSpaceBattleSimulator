package gsbs.flagshipaisystem;

import java.util.*;

import gsbs.common.components.*;
import gsbs.common.data.*;
import gsbs.common.data.enums.Teams;
import gsbs.common.entities.Entity;
import gsbs.common.entities.Flagship;

import gsbs.common.services.IProcess;
import gsbs.common.util.ThetaStar;
import gsbs.common.math.Distance;

import java.util.List;

public class FlagshipAIControlSystem implements IProcess {
    private Entity thisFlagship;
    private Entity targetFlagship;
    private List<Node> path;

    public void process(GameData gameData, World world) {
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

    private void handleOffensiveAction(GameData gameData, World world, double directionDifference) {
        Weapon weapon = thisFlagship.getComponent(Weapon.class);
        // FIX! change weapon correctly
        // shoot pistol when facing player, shoot shotgun when at a larger angle
        if (directionDifference > 2 * Math.PI - 0.3 || directionDifference < 0.3) {
            // Pistol
            weapon.changeWeapon();
            weapon.fire(thisFlagship, gameData, world);
            System.out.println("shoot pistol");
        } else {
            // Shotgun
            weapon.changeWeapon();
            weapon.fire(thisFlagship, gameData, world);
            System.out.println("shoot shot gun");
        }
    }

    private void handlePathfinding(GameData gameData, World world, Entity thisFlagship, Entity targetFlagship) {
        Grid grid = gameData.getGrid();
        var positionAIShip = thisFlagship.getComponent(Position.class);
        var positionTarget = targetFlagship.getComponent(Position.class);
        var spriteAIShip = thisFlagship.getComponent(Sprite.class);
        var spriteTarget = targetFlagship.getComponent(Sprite.class);
        float euclideanDistance = Distance.euclideanDistance(positionAIShip.getX(), positionAIShip.getY(), positionTarget.getX(), positionTarget.getY());

        Node start = grid.getNodeFromCoords((int) positionAIShip.getX() + spriteAIShip.getWidth() / 2, (int) positionAIShip.getY() + spriteAIShip.getHeight() / 2);
        Node goal = grid.getNodeFromCoords((int) positionTarget.getX() + spriteTarget.getWidth() / 2, (int) positionTarget.getY() + spriteTarget.getHeight() / 2);
        var movementAIShip = thisFlagship.getComponent(Movement.class);

        movementAIShip.setLeft(false);
        movementAIShip.setRight(false);


        ThetaStar thetaStar = new ThetaStar();
        List<Node> newPath = thetaStar.findPath(start, goal, grid);
        if (newPath != null) {
            path = newPath;
        } else {
            newPath = new ArrayList<>();
            newPath.add(goal);
            newPath.add(start);
            path = newPath;
        }

        // For debug (Assumed)
        gameData.setPath(path);
        System.out.println("Path: " + path);

        // Handle turning towards target node
        if (path.size() > 1) {
            int[] desiredLocation = grid.getCoordsFromNode(path.get(path.size() - 2));
            if (grid.getNodeFromCoords(desiredLocation[0], desiredLocation[1]).isBlocked()) {
                System.out.println("THIS NODE IS BLOCKED: " + grid.getNodeFromCoords(desiredLocation[0], desiredLocation[1]));
            }
            double directionDifferenceNode1 = getDirectionDifference(positionAIShip.getX(), positionAIShip.getY(), desiredLocation[0], desiredLocation[1], positionAIShip.getRadians());

            if (directionDifferenceNode1 > Math.PI) {
                movementAIShip.setLeft(true);
            } else {
                movementAIShip.setRight(true);
            }

            if (euclideanDistance < 500) {
                handleOffensiveAction(gameData, world, directionDifferenceNode1);
            }
        }

        // Handle Tokyo Drift error (drifting into asteroids when turning after reaching target node)
        if (path.size() > 2) {
            int[] desiredLocation = grid.getCoordsFromNode(path.get(path.size() - 2));
            int[] NextDesiredLocation = grid.getCoordsFromNode(path.get(path.size() - 3));
            double directionDifferenceNode2 = getDirectionDifference(positionAIShip.getX(), positionAIShip.getY(), NextDesiredLocation[0], NextDesiredLocation[1], positionAIShip.getRadians());

            float distanceFromDesiredLocation = Distance.euclideanDistance(positionAIShip.getX(), positionAIShip.getY(), desiredLocation[0], desiredLocation[1]);
            // Check all conditions that cause a need to slow down the AI ship
            System.out.println(movementAIShip.getVelocity());
            if (distanceFromDesiredLocation < 200 && movementAIShip.getVelocity() > 30 && (directionDifferenceNode2 < 2 * Math.PI - 0.3 || directionDifferenceNode2 > 0.3)) {
                movementAIShip.setUp(false);
                System.out.println("********************");
                System.out.println("SLOW DOWN!");
                System.out.println(distanceFromDesiredLocation);
                System.out.println(movementAIShip.getVelocity());
                System.out.println(directionDifferenceNode2);
            }
        }
        movementAIShip.setUp(true);
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