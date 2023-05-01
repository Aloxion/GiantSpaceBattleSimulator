package gsbs.collision;

import gsbs.common.components.*;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.data.enums.Teams;
import gsbs.common.entities.Asteroid;
import gsbs.common.entities.Bullet;
import gsbs.common.entities.Entity;
import gsbs.common.services.IPostProcess;

public class CollisionControlSystem implements IPostProcess {

    @Override
    public void process(GameData gameData, World world) {

        for (Entity entity : world.getEntities()){
            for (Entity collisionEntity : world.getEntities()){
                if (entity.getComponent(Health.class) == null){
                    continue;
                }

                Health entityHealth = entity.getComponent(Health.class);
                Health collisionHealth = collisionEntity.getComponent(Health.class);

                if (entity.getID().equals(collisionEntity.getID())){
                    continue;
                }

                if (entityHealth.isDead()){
                    entity.remove(Sprite.class);
                    world.removeEntity(entity);
                }

                //Collides?

                if (isCollided(entity, collisionEntity)){
                    System.out.println("Current entity: " + entity + "\n" + "Collision Entity: " + collisionEntity);

                    if (collisionEntity instanceof Bullet) {
                        entityHealth.removeHealthPoints(1);
                        System.out.println("Entity: " + collisionEntity + "\n" + "Entity health: " + entityHealth.getHealthPoints());
                        world.removeEntity(collisionEntity);
                    } else if (entity instanceof Bullet) {
                        if (collisionHealth != null) {
                            collisionHealth.removeHealthPoints(1);
                        }
                        world.removeEntity(entity);
                    }

                    if (collisionEntity instanceof Asteroid){
                        entityHealth.removeHealthPoints(1);
                        System.out.println("Entity: " + collisionEntity + "\n" + "Entity health: " + entityHealth.getHealthPoints());
                    }
                }
            }
        }

    }
    private Boolean isCollided(Entity entity1, Entity entity2){

        Hitbox hitbox = entity1.getComponent(Hitbox.class);
        Hitbox hitbox2 = entity2.getComponent(Hitbox.class);

        if(hitbox == null || hitbox2 == null){
            return false;
        }

        if (areOnSameTeam(entity1, entity2)) {
            return false;
        }

        return hitbox.intersects(hitbox2);
    }

    private boolean areOnSameTeam(Entity entity1, Entity entity2) {
        Team team1 = entity1.getComponent(Team.class);
        Team team2 = entity2.getComponent(Team.class);

        return team1 != null && team2 != null && team1.isInSameTeam(entity2);
    }
}



