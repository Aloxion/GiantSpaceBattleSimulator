package gsbs.collision;

import gsbs.common.components.*;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.data.enums.Teams;
import gsbs.common.entities.*;
import gsbs.common.events.GameLoseEvent;
import gsbs.common.events.GameWinEvent;
import gsbs.common.math.Vector2;
import gsbs.common.services.IPostProcess;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CollisionControlSystem implements IPostProcess {

    private void handleCollisionEntity(Entity entity, GameData gameData, World world) {
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
                }
            }
        }
    }

    public void process(GameData gameData, World world) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // Process collisions
        List<Future<?>> tasks = new ArrayList<>();
        for (Entity entity : world.getEntities()) {
            tasks.add(executorService.submit(() -> handleCollisionEntity(entity, gameData, world)));
        }

        try {
            for (var task : tasks) {
                task.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Collision tasks were interrupted");
        }


        var grid = gameData.getGrid();
        // Bounce back for flagship
        for (Entity flagship : world.getEntities(Flagship.class)) {
            var position = flagship.getComponent(Position.class);
            if (grid.getNodeFromCoords((int) position.getX(), (int) position.getY()).isCollidable()) {
                var movement = flagship.getComponent(Movement.class);

                Vector2 direction = new Vector2(movement.getDx(), movement.getDy());
                Vector2 collisionForce = grid.getNodeFromCoords((int) position.getX(), (int) position.getY()).getCollisionVector();
                Vector2 newDirection = collisionForce.multiply(-Math.abs(2 * (float) collisionForce.dot(direction))).subtract(direction).multiply(-1);
                float dT = gameData.getDeltaTime();
                position.setX(position.getX() + newDirection.x * dT);
                position.setY(position.getY() + newDirection.y * dT);
                movement.setDx(newDirection.x);
                movement.setDy(newDirection.y);
            }
        }
        executorService.shutdown();
        // Bounce back for Carriers
        for (Entity carrier : world.getEntities(Carrier.class)) {
            var position = carrier.getComponent(Position.class);
            if (grid.getNodeFromCoords((int) position.getX(), (int) position.getY()).isCollidable()) {
                var movement = carrier.getComponent(Movement.class);

                Vector2 direction = new Vector2(movement.getDx(), movement.getDy());
                Vector2 collisionForce = grid.getNodeFromCoords((int) position.getX(), (int) position.getY()).getCollisionVector();
                Vector2 newDirection = collisionForce.multiply(-Math.abs(2 * (float) collisionForce.dot(direction))).subtract(direction).multiply(-1);
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
}



