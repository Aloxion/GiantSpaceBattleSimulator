package gsbs.weapon;

import gsbs.common.components.*;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.entities.Bullet;
import gsbs.common.entities.Entity;
import gsbs.common.math.Vector2;
import gsbs.common.services.IProcess;

import java.util.List;

public class BulletControlSystem implements IProcess {

    @Override
    public void process(GameData gameData, World world) {
        for(Entity bullet : world.getEntities(Bullet.class)){
            Movement movement = bullet.getComponent(Movement.class);
            Position position = bullet.getComponent(Position.class);
            bullet.add(new MySprite());

            movement.setUp(true);
            updateShape(bullet.getComponent(MySprite.class), position);
        }
    }

    private void updateShape(MySprite mySprite, Position position) {
        mySprite.setSprite("default-bullet.png", 30, 30, position);
    }

    public static Entity createBullet(Position position){
        float acceleration = 10000;
        float deacceleration = 20;
        float maxSpeed = 300;
        float rotationSpeed = 0;

        Entity bullet = new Bullet();
        bullet.add(new Movement(deacceleration, acceleration, maxSpeed, rotationSpeed));

        bullet.add(new Position(position.getX(), position.getY(), position.getRadians()));
        return bullet;
    }
}
