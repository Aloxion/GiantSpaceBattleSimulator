package gsbs.weapon;

import gsbs.common.components.*;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.data.enums.Teams;
import gsbs.common.entities.Bullet;
import gsbs.common.entities.Entity;
import gsbs.common.math.Vector2;
import gsbs.common.services.IProcess;

public class BulletControlSystem implements IProcess {

    @Override
    public void process(GameData gameData, World world) {
        for(Entity bullet : world.getEntities(Bullet.class)){
            updateAndRemoveIfDead(bullet, world);
            Movement movement = bullet.getComponent(Movement.class);
            movement.setUp(true);
            Hitbox hitbox = bullet.getComponent(Hitbox.class);
            Position position = bullet.getComponent(Position.class);
            updateHitbox(hitbox, position);

        }
    }

    private void updateAndRemoveIfDead(Entity bullet, World world){
        Health healthComponent = bullet.getComponent(Health.class);
        healthComponent.removeHealthPoints(1);

        if(healthComponent.isDead()){
            world.removeEntity(bullet);
        }
    }

/*    private void updateShape(MySprite mySprite, Position position) {
        mySprite.setSprite("default-bullet.png", 10, 10, position);
    }*/

    public static Entity createBullet(Position position, int duration){
        float acceleration = 10000;
        float deacceleration = 20;
        float maxSpeed = 300;
        float rotationSpeed = 0;

        Entity bullet = new Bullet();
        bullet.add(new Movement(deacceleration, acceleration, maxSpeed, rotationSpeed));
        bullet.add(new Position(position.getX(), position.getY(), position.getRadians()));
        bullet.add(new Sprite(BulletControlSystem.class.getResource("/default-bullet.png"), 10, 10));

        bullet.add(new Health(duration));
        return bullet;
    }

    private void updateHitbox(Hitbox hitbox, Position position){
        hitbox.set(position.getX(), position.getY());
    }
}
