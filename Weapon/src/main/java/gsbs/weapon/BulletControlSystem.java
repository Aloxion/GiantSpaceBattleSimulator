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
            updateAndRemoveIfDead(bullet, world);
            Movement movement = bullet.getComponent(Movement.class);
            Position position = bullet.getComponent(Position.class);
            bullet.add(new MySprite());

            movement.setUp(true);
            updateShape(bullet.getComponent(MySprite.class), position);
        }
    }

    private void updateAndRemoveIfDead(Entity bullet, World world){
        Health healthComponent = bullet.getComponent(Health.class);
        healthComponent.removeHealthPoints(1);

        if(healthComponent.isDead()){
            world.removeEntity(bullet);
        }
    }

    private void updateShape(MySprite mySprite, Position position) {
        mySprite.setSprite("default-bullet.png", 10, 10, position);
    }

    public static Entity createBullet(Position position, int duration){
        float acceleration = 10000;
        float deacceleration = 20;
        float maxSpeed = 300;
        float rotationSpeed = 0;

        Entity bullet = new Bullet();
        bullet.add(new Movement(deacceleration, acceleration, maxSpeed, rotationSpeed));

        bullet.add(new Position(position.getX(), position.getY(), position.getRadians()));

        bullet.add(new Health(duration));
        return bullet;
    }
}
