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
            movement.setUp(true);
        }
    }

    private void updateAndRemoveIfDead(Entity bullet, World world){
        Health healthComponent = bullet.getComponent(Health.class);
        healthComponent.removeHealthPoints(1);

        if(healthComponent.isDead()){
            world.removeEntity(bullet);
        }
    }
}
