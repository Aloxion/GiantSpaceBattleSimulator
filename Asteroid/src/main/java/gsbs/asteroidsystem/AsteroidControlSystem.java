package gsbs.asteroidsystem;

import gsbs.common.components.Graphics;
import gsbs.common.components.Movement;
import gsbs.common.components.Position;
import gsbs.common.data.GameData;
import gsbs.common.data.GameKeys;
import gsbs.common.data.World;
import gsbs.common.entities.Entity;
import gsbs.common.entities.Asteroid;
import gsbs.common.math.Vector2;
import gsbs.common.services.IProcess;
import java.util.List;

public class AsteroidControlSystem implements IProcess {


    @Override
    public void process(GameData gameData, World world) {

        // Create new asteroid (Cooldown?, Random?)
        if (Math.random() > 0.99){
            new AsteroidPlugin().start(gameData,world);
        }

        for (Entity asteroid : world.getEntities(Asteroid.class)) {
            var position = asteroid.getComponent(Position.class);

            var graphics = asteroid.getComponent(Graphics.class);
            updateShape(graphics, position);


        }
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