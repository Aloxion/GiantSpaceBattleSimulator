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

    private void handleOffensiveAction(GameData gameData, World world, boolean close){
        Weapon weapon = thisFlagship.getComponent(Weapon.class);
        weapon.changeWeapon();
        if (close){
            weapon.fire(thisFlagship, gameData, world);
        } else {
            weapon.changeWeapon();
            weapon.fire(thisFlagship, gameData, world);
        }

    }

    private void handlePathfinding(GameData gameData, World world, Entity thisFlagship, Entity targetFlagship){
        var positionAIShip = thisFlagship.getComponent(Position.class);
        var positionTarget = targetFlagship.getComponent(Position.class);
        var spriteAIShip = thisFlagship.getComponent(Sprite.class);
        var spriteTarget = targetFlagship.getComponent(Sprite.class);
        float euclideanDistance = Distance.euclideanDistance(positionAIShip.getX(), positionAIShip.getY(), positionTarget.getX(), positionTarget.getY());

        Node start = grid.getNodeFromCoords((int) positionAIShip.getX() + spriteAIShip.getWidth()/2, (int) positionAIShip.getY() + spriteAIShip.getHeight()/2);
        Node goal = grid.getNodeFromCoords((int) positionTarget.getX() + spriteTarget.getWidth()/2, (int) positionTarget.getY() + spriteTarget.getHeight()/2);
        var movementAIShip = thisFlagship.getComponent(Movement.class);
        if(euclideanDistance > 500) {
            movementAIShip.setUp(true);
        }
        else {
            handleOffensiveAction(gameData, world, true);
            movementAIShip.setUp(false);
        }

        movementAIShip.setLeft(false);
        movementAIShip.setRight(false);


        List<Node> thetaStarList = new ThetaStar().findPath(start, goal, grid);

        // For debug (Assumed)
        gameData.setPath(thetaStarList);

        int[] desiredLocation = new int[2];
        if (thetaStarList == null){
            movementAIShip.setUp(true);
            return;
        }
        if (thetaStarList.size() > 1){
            desiredLocation = grid.getCoordsFromNode(thetaStarList.get(thetaStarList.size()-2));
        }

        if (grid.getNodeFromCoords(desiredLocation[0], desiredLocation[1]).isBlocked()){
            System.out.println("THIS NODE IS BLOCKED: " + grid.getNodeFromCoords(desiredLocation[0], desiredLocation[1]));
        }

        double desiredAngle = convertToUnitCircle(getDirection(positionAIShip.getX(), positionAIShip.getY(), desiredLocation[0], desiredLocation[1]));
        double currUnit = convertToUnitCircle(positionAIShip.getRadians());
        double dirDiff = ((2*Math.PI - currUnit) + desiredAngle) % (2*Math.PI);

        if(dirDiff > Math.PI){
            movementAIShip.setLeft(true);
        } else {
            movementAIShip.setRight(true);
        }

    }

    private double getDirection(float x1, float y1, float x2, float y2){
        return Math.atan2(y2 - y1, x2 - x1);

    }

    private double convertToUnitCircle(double dir){
        return 2*Math.PI - ((dir % (2 * Math.PI) + 2 * Math.PI) % (2 * Math.PI));
    }
}
