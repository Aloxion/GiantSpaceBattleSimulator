package gsbs.flagshipsystem;

import gsbs.common.components.Graphics;
import gsbs.common.components.Movement;
import gsbs.common.components.Position;
import gsbs.common.components.Team;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.entities.Entity;
import gsbs.common.entities.Flagship;
import gsbs.common.services.IPlugin;

public class FlagshipPlugin implements IPlugin {
    private Entity playerFlagship;
    private Entity enemyFlagship;


    @Override
    public void start(GameData gameData, World world) {
        playerFlagship = createFlagship(gameData, world, 1, gameData.getDisplayWidth() / 10.0f, gameData.getDisplayHeight() / 2.0f, 0);
        world.addEntity(playerFlagship);

        enemyFlagship = createFlagship(gameData, world, 2, gameData.getDisplayWidth() - gameData.getDisplayWidth() / 10.0f, gameData.getDisplayHeight() / 2.0f, (float) Math.PI);
        world.addEntity(enemyFlagship);
    }

    @Override
    public void stop(GameData gameData, World world) {
        System.out.println("Stop player");
    }


    private Entity createFlagship(GameData gameData, World world, int teamNumber, float x, float y, float radians) {

        float deacceleration = 10;
        float acceleration = 12.5f;
        float maxSpeed = 10;
        float rotationSpeed = 0.4f;

        Entity Ship = new Flagship();
        Ship.add(new Graphics());
        Ship.add(new Movement(deacceleration, acceleration, maxSpeed, rotationSpeed));
        Ship.add(new Position(x, y, radians));
        Ship.add(new Team(teamNumber));

        return Ship;
    }
}
