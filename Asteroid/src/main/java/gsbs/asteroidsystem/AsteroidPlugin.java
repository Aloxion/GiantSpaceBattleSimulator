package gsbs.asteroidsystem;

import gsbs.common.components.Graphics;
import gsbs.common.components.Movement;
import gsbs.common.components.Position;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.entities.Entity;
import gsbs.common.entities.Asteroid;
import gsbs.common.services.IPlugin;

public class AsteroidPlugin implements IPlugin {
    private Entity asteroid;

    @Override
    public void start(GameData gameData, World world) {
        asteroid = createAsteroid(gameData, world);
        world.addEntity(asteroid);
        System.out.println(world.getEntities());
    }

    @Override
    public void stop(GameData gameData, World world) {
        System.out.println("Stop player");
    }

    private Entity createAsteroid(GameData gameData, World world) {

        float deacceleration = 10;
        float acceleration = 50;
        float maxSpeed = 100;
        float rotationSpeed = 5;
        float x = (float) (gameData.getDisplayWidth() / (Math.random()*5));
        float y = (float) (gameData.getDisplayHeight() / (Math.random()*5));
        float radians = (float) (3.1415f / (Math.random()*5));

        Entity Asteroid = new Asteroid();
        Asteroid.add(new Graphics());
        Asteroid.add(new Movement(deacceleration, acceleration, maxSpeed, rotationSpeed));
        Asteroid.add(new Position(x, y, radians));

        return Asteroid;
    }
}
