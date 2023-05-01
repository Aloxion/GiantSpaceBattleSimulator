package gsbs.collision;

import gsbs.common.components.*;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.entities.Asteroid;
import gsbs.common.entities.Bullet;
import gsbs.common.entities.Entity;
import gsbs.common.services.IPostProcess;

public class CollisionControlSystem implements IPostProcess {

    @Override
    public void process(GameData gameData, World world) {

        for (Entity entity : world.getEntities()){
            for (Entity collisionEntity : world.getEntities()){
                if (entity.getComponent(Health.class) == null || collisionEntity.getComponent(Health.class) == null){
                    continue;
                }


                Health entityHealth = entity.getComponent(Health.class);
                Health collisionEntityHealth = collisionEntity.getComponent(Health.class);

                if (entity.getID().equals(collisionEntity.getID())){
                    continue;
                }

                if (entity.getClass() == collisionEntity.getClass()){
                    continue;
                }

                if (entityHealth.isDead()){
                    entity.remove(Sprite.class);
                    world.removeEntity(entity);


                    if (collisionEntityHealth.isDead()){
                        collisionEntity.remove(Sprite.class);
                        world.removeEntity(collisionEntity);
                    }
                }


                //Collides?

                if (isCollided(entity, collisionEntity)){
                        entityHealth.removeHealthPoints(1);

                        if (entityHealth.isDead()){
                            entity.remove(Sprite.class);
                            world.removeEntity(entity);
                        }else if(entity.getClass().equals(Bullet.class)){
                            entity.remove(Sprite.class);
                            world.removeEntity(entity);
                        }
                }
            }

        }

    }


    private Boolean isCollided(Entity entity, Entity entity2){

        Hitbox hitbox = entity.getComponent(Hitbox.class);
        Hitbox hitbox1 = entity2.getComponent(Hitbox.class);

        if(hitbox == null || hitbox1 == null){
            System.out.println("Collision");

            return false;
        }

        if (entity.getComponent(Team.class).getTeam() == entity2.getComponent(Team.class).getTeam()){
            System.out.println("Friendly");
            return false;
        }
        return hitbox.intersects(hitbox1);
    }

}



