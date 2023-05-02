package gsbs.collision;

import gsbs.common.components.Health;
import gsbs.common.components.Hitbox;
import gsbs.common.components.Sprite;
import gsbs.common.components.Team;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.data.enums.Teams;
import gsbs.common.entities.Asteroid;
import gsbs.common.entities.Bullet;
import gsbs.common.entities.Entity;
import gsbs.common.entities.Flagship;
import gsbs.common.events.GameLoseEvent;
import gsbs.common.events.GameWinEvent;
import gsbs.common.services.IPostProcess;

public class CollisionControlSystem implements IPostProcess {

    @Override
    public void process(GameData gameData, World world) {
        for (Entity entity : world.getEntities()) {
            for (Entity collisionEntity : world.getEntities()) {
                if (entity.getComponent(Health.class) == null) {
                    continue;
                }

                Health entityHealth = entity.getComponent(Health.class);
                Health collisionHealth = collisionEntity.getComponent(Health.class);

                if (entity.getID().equals(collisionEntity.getID())) {
                    continue;
                }

                if (entityHealth.isDead()) {
                    entity.remove(Sprite.class);
                    world.removeEntity(entity);

                    if (entity instanceof Flagship && entity.getComponent(Team.class) != null) {
                        if (entity.getComponent(Team.class).getTeam() == Teams.PLAYER) {
                            gameData.addEvent(new GameLoseEvent(null));
                        }

                        if (entity.getComponent(Team.class).getTeam() == Teams.ENEMY) {
                            gameData.addEvent(new GameWinEvent(null));
                        }
                    }
                }

                //Collides?
                if (isCollided(entity, collisionEntity)) {
                    if (collisionEntity instanceof Bullet) {
                        entityHealth.removeHealthPoints(1);
                        world.removeEntity(collisionEntity);
                    } else if (entity instanceof Bullet) {
                        if (collisionHealth != null) {
                            collisionHealth.removeHealthPoints(1);
                        }
                        world.removeEntity(entity);
                    }

                    if (collisionEntity instanceof Asteroid) {
                        entityHealth.removeHealthPoints(1);
                    }
                }
            }
        }
    }

    private Boolean isCollided(Entity entity1, Entity entity2) {
        Hitbox hitbox = entity1.getComponent(Hitbox.class);
        Hitbox hitbox2 = entity2.getComponent(Hitbox.class);

        if (hitbox == null || hitbox2 == null) {
            return false;
        }

        if (entity1.getComponent(Team.class).isInSameTeam(entity2)) {
            return false;
        }

        return hitbox.intersects(hitbox2);
    }
}



