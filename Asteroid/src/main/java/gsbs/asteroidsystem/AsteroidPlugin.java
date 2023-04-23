package gsbs.asteroidsystem;

import gsbs.common.components.Position;
import gsbs.common.components.MySprite;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.entities.Entity;
import gsbs.common.entities.Asteroid;
import gsbs.common.services.IPlugin;

import java.util.HashMap;
import java.util.Map;

public class AsteroidPlugin implements IPlugin {
    private Entity asteroid;

    @Override
    public void start(GameData gameData, World world) {
        for (int i = 0; i < 3; i++) {
            asteroid = createAsteroid(gameData, world);
            world.addEntity(asteroid);
        }
    }

    @Override
    public void stop(GameData gameData, World world) {

    }

    private Entity createAsteroid(GameData gameData, World world) {

        int xmin = gameData.getDisplayWidth()/4;// Minimum value of range
        int xmax = gameData.getDisplayWidth() - ((gameData.getDisplayWidth()/4));
        int ymin = gameData.getDisplayHeight()/4;
        int ymax = gameData.getDisplayHeight() - ((gameData.getDisplayHeight()/4));

        float x = (float) (Math.floor(Math.random() * (xmax - xmin + 1) + xmin));
        float y = (float) (Math.floor(Math.random() * (ymax - ymin + 1) + ymin));
        float radians = (float) (3.1415f / (Math.random()*5));

        Entity Asteroid = new Asteroid();
        Asteroid.add(new MySprite());
        Asteroid.add(new Position(x, y, radians));
        updateShape(Asteroid.getComponent(MySprite.class), Asteroid.getComponent(Position.class));

        return Asteroid;
    }
    private void updateShape(MySprite mySprite, Position position) {

        mySprite.setSprite("asteroid/default-asteroid.png", 256/2, 256/2, position);
    }
}
