package gsbs.attackshipsystem;

import gsbs.attackshipsystem.components.Boid;
import gsbs.common.components.*;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.entities.Attackship;
import gsbs.common.entities.Entity;
import gsbs.common.events.Event;
import gsbs.common.events.SpawnAttackships;
import gsbs.common.math.Vector2;
import gsbs.common.services.IEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AttackshipSpawner implements IEventListener {
    private final int maxSpawnRetries = 50;

    @Override
    public void onEvent(Event event, GameData gameData) {
        if (event instanceof SpawnAttackships) {
            World world = ((SpawnAttackships) event).getWorld();
            int amount = ((SpawnAttackships) event).getAmount();
            spawnAttackship(event.getSource(), world, amount);
        }
    }

    private void spawnAttackship(Entity leader, World world, int amount) {
        Random random = new Random();
        List<Entity> attackShips = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            for (int spawnRetries = 0; spawnRetries < this.maxSpawnRetries; spawnRetries++) {
                Position leaderPositionComponent = leader.getComponent(Position.class);
                Vector2 flagshipPosition = new Vector2(leaderPositionComponent.getX(), leaderPositionComponent.getY());
                float spawnRadius = 100;

                Vector2 attackshipPosition = flagshipPosition.add(new Vector2(random.nextFloat() * spawnRadius, random.nextFloat() * spawnRadius));

                Attackship attackship = new Attackship();
                attackship.add(new Position(attackshipPosition.x, attackshipPosition.y, 0));
                attackship.add(new Sprite(getClass().getResource("/assets2/flagship.png"), 20, 20));
                attackship.add(new Health(1));
                attackship.add(new Hitbox(10, 10, attackshipPosition.x, attackshipPosition.y));
                attackship.add(new Team(leader.getComponent(Team.class).getTeam()));
                switch (leader.getComponent(Team.class).getTeam()) {
                    case PLAYER:
                        attackship.add(new Weapon(List.of(new Laser("/player_laser.png"))));
                        break;
                    case ENEMY:
                        attackship.add(new Weapon(List.of(new Laser("/enemy_laser.png"))));
                        break;
                }

                Boid boid = new Boid(leader);
                boid.velocity = new Vector2(random.nextFloat() * 2 - 1, random.nextFloat() * 4 - 2);
                attackship.add(boid);

                // Check if there are any collisions with the world
                var collision = false;
                for (var collidable : world.getEntitiesWithComponent(Hitbox.class)) {
                    if (collidable instanceof Attackship)
                        continue;

                    var collidableHitbox = collidable.getComponent(Hitbox.class);
                    var attackshipHitbox = attackship.getComponent(Hitbox.class);

                    if (collidableHitbox.intersects(attackshipHitbox)) {
                        collision = true;
                        break;
                    }
                }

                if (!collision) {
                    attackShips.add(attackship);
                    break;
                }
            }
        }

        attackShips.forEach(world::addEntity);
    }
}
