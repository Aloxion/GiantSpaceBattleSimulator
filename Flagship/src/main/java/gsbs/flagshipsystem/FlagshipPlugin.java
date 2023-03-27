package gsbs.flagshipsystem;

import gsbs.common.components.Graphics;
import gsbs.common.components.Movement;
import gsbs.common.components.Position;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.entities.Entity;
import gsbs.common.entities.Flagship;
import gsbs.common.services.IPlugin;

public class FlagshipPlugin implements IPlugin {
    private Entity flagship;


    @Override
    public void start(GameData gameData, World world) {
        flagship = createFlagship(gameData, world);
        world.addEntity(flagship);
    }

    @Override
    public void stop(GameData gameData, World world) {
        System.out.println("Stop player");
    }


    private Entity createFlagship(GameData gameData, World world) {

        float deacceleration = 10;
        float acceleration = 200;
        float maxSpeed = 300;
        float rotationSpeed = 5;
        float x = gameData.getDisplayWidth() / 2.0f;
        float y = gameData.getDisplayHeight() / 2.0f;
        float radians = 3.1415f / 2;

        Entity Ship = new Flagship();
        Ship.add(new Graphics());
        Ship.add(new Movement(deacceleration, acceleration, maxSpeed, rotationSpeed));
        Ship.add(new Position(x, y, radians));

        return Ship;
    }
}
