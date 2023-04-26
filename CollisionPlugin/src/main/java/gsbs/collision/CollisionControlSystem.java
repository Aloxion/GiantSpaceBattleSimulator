package gsbs.collision;

import gsbs.common.components.Health;
import gsbs.common.components.Position;
import gsbs.common.components.Sprite;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
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

                    if (!entityHealth.isDead()){
                        entityHealth.removeHealthPoints(1);

                        if (entityHealth.isDead()){
                            entity.remove(Sprite.class);
                            world.removeEntity(entity);
                        }
                    }
                }
            }

        }

    }


    private Boolean isCollided(Entity entity, Entity entity2){
        Position entMov = entity.getComponent(Position.class);
        Position entMov2 = entity2.getComponent(Position.class);

        if(entity.getComponent(Sprite.class) == null || entity2.getComponent(Sprite.class) == null){
            return false;
        }

        Sprite sprite = entity.getComponent(Sprite.class);
        Sprite sprite2 = entity2.getComponent(Sprite.class);
        
        float dx = entMov.getX() - entMov2.getX();
        float dy = entMov.getY() - entMov2.getY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        float width = (float) (sprite.getWidth() + sprite2.getWidth())/2.0f;
        float height = (float) (sprite.getHeight() + sprite2.getHeight()) / 2.0f;
        if (distance < width || distance < height) {
            System.out.println("Distance: " +distance +"\n" + "number: "+ width);
            return true;
        }


        return false;
    }

}



