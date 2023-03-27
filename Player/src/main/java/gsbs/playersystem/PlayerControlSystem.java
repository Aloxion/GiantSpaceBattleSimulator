package gsbs.playersystem;


import gsbs.common.components.Graphics;
import gsbs.common.components.Movement;
import gsbs.common.components.Position;
import gsbs.common.data.GameData;
import gsbs.common.data.GameKeys;
import gsbs.common.data.World;
import gsbs.common.entities.Entity;
import gsbs.common.entities.Player;
import gsbs.common.math.Vector2;
import gsbs.common.services.IProcess;

import java.util.List;

public class PlayerControlSystem implements IProcess {

    @Override
    public void process(GameData gameData, World world) {
        for (Entity player : world.getEntities(Player.class)) {
            var position = player.getComponent(Position.class);
            var movement = player.getComponent(Movement.class);
            movement.setLeft(gameData.getKeys().isDown(GameKeys.Keys.LEFT));
            movement.setRight(gameData.getKeys().isDown(GameKeys.Keys.RIGHT));
            movement.setUp(gameData.getKeys().isDown(GameKeys.Keys.UP));

            var graphics = player.getComponent(Graphics.class);
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
        p3.x = (float) (x + Math.cos(radians + 3.1415f) * 5);
        p3.y = (float) (y + Math.sin(radians + 3.1415f) * 5);

        var p4 = new Vector2();
        p4.x = (float) (x + Math.cos(radians + 4 * 3.1415f / 5) * 8);
        p4.y = (float) (y + Math.sin(radians + 4 * 3.1415f / 5) * 8);

        graphics.setShape(List.of(new Vector2[]{p1, p2, p3, p4}));
    }
}