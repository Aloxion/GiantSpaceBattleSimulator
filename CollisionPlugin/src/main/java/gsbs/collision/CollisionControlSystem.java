package gsbs.collision;

import gsbs.common.components.*;
import gsbs.common.data.GameData;
import gsbs.common.data.Node;
import gsbs.common.data.World;
import gsbs.common.data.enums.Teams;
import gsbs.common.entities.Asteroid;
import gsbs.common.entities.Bullet;
import gsbs.common.entities.Entity;
import gsbs.common.entities.Flagship;
import gsbs.common.events.GameLoseEvent;
import gsbs.common.events.GameWinEvent;
import gsbs.common.math.Vector2;
import gsbs.common.services.IPostProcess;

import java.util.Random;
import java.util.Vector;

public class CollisionControlSystem implements IPostProcess {

    public void process(GameData gameData, World world) {
        // Process collisions
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
                    } else if (collisionEntity instanceof Asteroid) {
                        entityHealth.removeHealthPoints(1);
                    } else {
                        entityHealth.removeHealthPoints(1);
                    }
                }
            }
        }

        // Bounce back
        for (Entity flagship : world.getEntities(Flagship.class)) {
            var grid = gameData.getGrid();
            var position = flagship.getComponent(Position.class);
            if(grid.getNodeFromCoords((int)position.getX(), (int)position.getY()).isCollidable()){
                var movement = flagship.getComponent(Movement.class);

                Vector2 direction = new Vector2(movement.getDx(), movement.getDy());
                System.out.println(flagship.getComponent(Team.class).getTeam());
                Vector2 collisionForce = grid.getNodeFromCoords((int)position.getX(), (int)position.getY()).getCollisionVector();
                Vector2 newDirection = collisionForce.multiply(2 * (float) collisionForce.dot(direction)).subtract(direction).multiply(-1);
                float dT = gameData.getDeltaTime();
                position.setX(position.getX() + newDirection.x * dT);
                position.setY(position.getY() + newDirection.y * dT);
                movement.setDx(newDirection.x);
                movement.setDy(newDirection.y);
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

    private void rebound(Entity flagship, Entity asteroid){
        System.out.println("Rebound");
        if(flagship instanceof Flagship){
            System.out.println("FLAAG");
        float flagX = flagship.getComponent(Position.class).getX();
        float flagY = flagship.getComponent(Position.class).getY();

        float astX = asteroid.getComponent(Position.class).getX();
        float astY = asteroid.getComponent(Position.class).getY();

        Vector2 flagVector = new Vector2(flagX,flagY);
        Vector2 astVector = new Vector2(astX,astY);

        double dotProduct = flagVector.dot(astVector);
        double magnitudeProduct = flagVector.magnitude() * astVector.magnitude();
        float angle = (float)Math.acos(dotProduct / magnitudeProduct);

        flagship.getComponent(Position.class).setRadians(angle*2);
        flagship.getComponent(Movement.class).setAcceleration(-100);
        }
        else{
            float astX = flagship.getComponent(Position.class).getX();
            float astY = flagship.getComponent(Position.class).getY();

            float flagX = asteroid.getComponent(Position.class).getX();
            float flagY = asteroid.getComponent(Position.class).getY();

            Vector2 flagVector = new Vector2(flagX,flagY);
            Vector2 astVector = new Vector2(astX,astY);

            double dotProduct = flagVector.dot(astVector);
            double magnitudeProduct = flagVector.magnitude() * astVector.magnitude();
            float angle = (float)Math.acos(dotProduct / magnitudeProduct);

            flagship.getComponent(Position.class).setRadians(angle*2);
            flagship.getComponent(Movement.class).setAcceleration(-flagship.getComponent(Movement.class).getAcceleration());
        }
    }
}



