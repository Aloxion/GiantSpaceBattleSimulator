package gsbs.playersystem;

import gsbs.common.components.Graphics;
import gsbs.common.components.Movement;
import gsbs.common.components.Position;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.entities.Entity;
import gsbs.common.entities.Player;
import gsbs.common.services.IGamePluginService;

public class PlayerPlugin implements IGamePluginService {
    private Entity player;

    @Override
    public void start(GameData gameData, World world) {
        player = createPlayerShip(gameData, world);
        world.addEntity(player);
    }

    @Override
    public void stop(GameData gameData, World world) {
        System.out.println("Stop player");
    }


    private Entity createPlayerShip(GameData gameData, World world) {

        float deacceleration = 10;
        float acceleration = 200;
        float maxSpeed = 300;
        float rotationSpeed = 5;
        float x = gameData.getDisplayWidth() / 2.0f;
        float y = gameData.getDisplayHeight() / 2.0f;
        float radians = 3.1415f / 2;

        Entity playerShip = new Player();
        playerShip.add(new Graphics());
        playerShip.add(new Movement(deacceleration, acceleration, maxSpeed, rotationSpeed));
        playerShip.add(new Position(x, y, radians));

        return playerShip;
    }
}
