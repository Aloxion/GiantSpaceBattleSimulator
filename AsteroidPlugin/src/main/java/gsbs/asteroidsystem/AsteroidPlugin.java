package gsbs.asteroidsystem;

import gsbs.common.components.Position;
import gsbs.common.components.Sprite;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.data.enums.AsteroidSizes;
import gsbs.common.entities.Asteroid;
import gsbs.common.entities.Entity;
import gsbs.common.services.IPlugin;

public class AsteroidPlugin implements IPlugin {
    @Override
    public void start(GameData gameData, World world) {
        for (int i = 0; i < 16; i++) {
            Entity asteroid = createAsteroid(gameData, world);
            world.addEntity(asteroid);
        }
    }

    @Override
    public void stop(GameData gameData, World world) {
        for (var asteroid : world.getEntities(Asteroid.class)) {
            world.removeEntity(asteroid);
        }
    }

    private Entity createAsteroid(GameData gameData, World world) {
        int xmin = gameData.getDisplayWidth() / 4;// Minimum value of range
        int xmax = gameData.getDisplayWidth() - ((gameData.getDisplayWidth() / 4));
        int ymin = gameData.getDisplayHeight() / 4;
        int ymax = gameData.getDisplayHeight() - ((gameData.getDisplayHeight() / 4));

        float x = (float) (Math.floor(Math.random() * (xmax - xmin + 1) + xmin));
        float y = (float) (Math.floor(Math.random() * (ymax - ymin + 1) + ymin));

        float radians = (float) (3.1415f / (Math.random() * 5));

        try {
            Entity Asteroid = new Asteroid();
            int size = AsteroidSizes.randomDirection().getSize();
            Asteroid.add(new Sprite(getClass().getResource("/assets/default-asteroid.png"), size, size));
            Asteroid.add(new Position(x, y, radians));

            return Asteroid;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
