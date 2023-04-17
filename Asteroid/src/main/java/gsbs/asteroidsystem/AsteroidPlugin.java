package gsbs.asteroidsystem;

import gsbs.common.components.Graphics;
import gsbs.common.components.Position;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.entities.Entity;
import gsbs.common.entities.Asteroid;
import gsbs.common.math.Vector2;
import gsbs.common.services.IPlugin;
import java.util.List;

public class AsteroidPlugin implements IPlugin {
    private Entity asteroid;

    @Override
    public void start(GameData gameData, World world) {
        asteroid = createAsteroid(gameData, world);
        world.addEntity(asteroid);
    }

    @Override
    public void stop(GameData gameData, World world) {

    }

    private Entity createAsteroid(GameData gameData, World world) {

        int min = 100; // Minimum value of range
        int max = 400; // Max spawn value

        float x = (float) (Math.floor(Math.random() * (max - min + 1) + min));
        float y = (float) (gameData.getDisplayHeight() / (Math.random()*5));
        float radians = (float) (3.1415f / (Math.random()*5));

        Entity Asteroid = new Asteroid();
        Asteroid.add(new Graphics());
        Asteroid.add(new Position(x, y, radians));
        updateShape(Asteroid.getComponent(Graphics.class), Asteroid.getComponent(Position.class));

        return Asteroid;
    }
    private void updateShape(Graphics graphics, Position position) {

        float x = position.getX();
        float y = position.getY();
        float radians = position.getRadians();

        var p1 = new Vector2();
        p1.x = (float) (x + Math.cos(radians) * 8);
        p1.y = (float) (y + Math.sin(radians) * 8);

        var p2 = new Vector2();
        p2.x = (float) (x + Math.cos(radians - 4 * 3.1415f / 5) * 8);
        p2.y = (float) (y + Math.sin(radians - 4 * 3.1145f / 5) * 8);

        var p3 = new Vector2();
        p3.x = (float) (x + Math.cos(radians + 3.1415f) * 15);
        p3.y = (float) (y + Math.sin(radians + 3.1415f) * 15);

        var p4 = new Vector2();
        p4.x = (float) (x + Math.cos(radians + 4 * 3.1415f / 5) * 8);
        p4.y = (float) (y + Math.sin(radians + 4 * 3.1415f / 5) * 8);

        graphics.setShape(List.of(new Vector2[]{p1, p2, p3, p4}));
    }
}
