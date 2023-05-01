package gsbs.asteroidsystem;

import gsbs.common.components.Hitbox;
import gsbs.common.components.Position;
import gsbs.common.components.Sprite;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.data.enums.AsteroidSizes;
import gsbs.common.entities.Asteroid;
import gsbs.common.entities.Entity;
import gsbs.common.services.IPlugin;

public class AsteroidPlugin implements IPlugin {

    private static final int MAX_ATTEMPTS = 30;
    @Override
    public void start(GameData gameData, World world) {
        for (int i = 0; i < 16; i++) {
            Entity asteroid = createAsteroid(gameData, world);
            if (asteroid != null){
                world.addEntity(asteroid);
            } else {
                i--;
            }
        }
    }

    @Override
    public void stop(GameData gameData, World world) {
        for (var asteroid : world.getEntities(Asteroid.class)) {
            world.removeEntity(asteroid);
        }
    }

    private Entity createAsteroid(GameData gameData, World world) {
        float radians = (float) (3.1415f / (Math.random() * 5));

            Entity asteroid = new Asteroid();
            int size = AsteroidSizes.randomDirection().getSize();
            float fsize = (float) size;

            asteroid.add(new Hitbox(fsize, fsize, getRandomX(gameData), getRandomY(gameData)));
            asteroid.add(new Position(asteroid.getComponent(Hitbox.class).getX(), asteroid.getComponent(Hitbox.class).getY(), radians));
            asteroid.add(new Sprite(getClass().getResource("/assets/default-asteroid.png"), size, size));

            int attempts = 0;
            boolean overlapping = true;
            while (overlapping && attempts < MAX_ATTEMPTS) {
                overlapping = false;
                for (Entity otherAsteroid : world.getEntities(Asteroid.class)) {
                    if (asteroid == otherAsteroid) {
                        continue;
                    }
                    Hitbox otherH = otherAsteroid.getComponent(Hitbox.class);
                    Hitbox currentH = asteroid.getComponent(Hitbox.class);

                    if (currentH.intersects(otherH)) {
                        Position pos = asteroid.getComponent(Position.class);
                        pos.setX(getRandomX(gameData));
                        pos.setY(getRandomY(gameData));
                        currentH.set(pos.getX(), pos.getY());
                        overlapping = true;
                        break;
                    }
                }
                attempts++;
            }

            if (overlapping) {
                return null;
            }
            return asteroid;
        }
    private float getRandomX(GameData gameData){
        int xmin = gameData.getDisplayWidth() / 6;
        int xmax = gameData.getDisplayWidth()+100 - (gameData.getDisplayWidth() / 2);
        return (float) (Math.floor(Math.random() * (xmax - xmin + 1) + xmin));
    }

    private float getRandomY(GameData gameData){
        int ymin = gameData.getDisplayHeight() / 8;
        int ymax = gameData.getDisplayHeight() - (gameData.getDisplayHeight() / 2);
        return (float) (Math.floor(Math.random() * (ymax - ymin + 1) + ymin));
    }
}
