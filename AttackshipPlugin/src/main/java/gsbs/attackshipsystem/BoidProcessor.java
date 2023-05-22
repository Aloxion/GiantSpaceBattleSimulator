package gsbs.attackshipsystem;

import gsbs.attackshipsystem.components.Boid;
import gsbs.common.components.Hitbox;
import gsbs.common.components.Position;
import gsbs.common.components.Team;
import gsbs.common.components.Weapon;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.entities.Attackship;
import gsbs.common.entities.Entity;
import gsbs.common.events.SpawnAttackships;
import gsbs.common.math.KDTree;
import gsbs.common.math.Vector2;
import gsbs.common.services.IProcess;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


public class BoidProcessor implements IProcess {
    private static final boolean DEBUG_MENU = false;
    private float collisionAvoidanceFactor = 10.f;
    private float edgesFactor = 0.5f;
    private float maxForce = 0.01f;
    private float maxSpeed = 1.6f;
    private float alignmentFactor = 0.05f;
    private float alignmentRadius = 25;
    private float separationFactor = 1.5f;
    private float separationRadius = 40;
    private float cohesionFactor = 0.05f;
    private float cohesionRadius = 30;
    private float leaderFactor = 5f;
    private float leaderDistance = 50;

    private KDTree.Euclidean<Entity> boids = null;
    private List<Entity> worldCollidables = null;

    private static float floatSlider(float value, float min, float max, String name) {
        float[] flt = new float[1];
        flt[0] = value;
        ImGui.sliderFloat(name, flt, min, max);
        return flt[0];
    }

    @Override
    public void process(GameData gameData, World world) {
        if (DEBUG_MENU) {
            debugMenu();
        }

        // If the leader is dead, then we are dead
        for (var boidEntity : world.getEntitiesWithComponent(Boid.class)) {
            if (world.getEntity(boidEntity.getComponent(Boid.class).leader.getID()) == null) {
                world.removeEntity(boidEntity);
            }
        }

        // Fix position
        for (var entity : world.getEntities(Attackship.class)) {
            var hitbox = entity.getComponent(Hitbox.class);
            var position = entity.getComponent(Position.class);
            hitbox.set(position.getX() + hitbox.getWidth() * 0.5f, position.getY() + hitbox.getHeight() * 0.5f);
        }

        // Sync boid position
        for (var boidEntity : world.getEntitiesWithComponent(Boid.class)) {
            // Sync boid position
            Position boidPosition = boidEntity.getComponent(Position.class);
            Boid boid = boidEntity.getComponent(Boid.class);
            boid.position.x = boidPosition.getX();
            boid.position.y = boidPosition.getY();
        }

        // Create a kd-tree of all the boids
        boids = new KDTree.Euclidean<>(2);
        for (var boidEntity : world.getEntitiesWithComponent(Boid.class)) {
            Position boidPosition = boidEntity.getComponent(Position.class);
            boids.addPoint(new double[]{boidPosition.getX(), boidPosition.getY()}, boidEntity);
        }

        // Cache all collidables in the world
        worldCollidables = new ArrayList<>();
        for (Entity collidable : world.getEntitiesWithComponent(Hitbox.class)) {
            if (collidable.getComponent(Team.class) != null)
                continue;
            worldCollidables.add(collidable);
        }

        for (var boidEntity : world.getEntitiesWithComponent(Boid.class)) {
            Position boidPosition = boidEntity.getComponent(Position.class);
            Boid boid = boidEntity.getComponent(Boid.class);

            // Flocking
            var alignment = align(boid, world);
            var cohesion = cohesion(boid, world);
            var separation = separation(boid, world);
            var edges = edges(boid, gameData);
            var leader = leader(boid, world);
            var collisionAvoidance = collisionAvoidance(boid, world);


            alignment = alignment.multiply(this.alignmentFactor);
            cohesion = cohesion.multiply(this.cohesionFactor);
            separation = separation.multiply(this.separationFactor);
            edges = edges.multiply(this.edgesFactor);
            leader = leader.multiply(this.leaderFactor);
            collisionAvoidance = collisionAvoidance.multiply(this.collisionAvoidanceFactor);

            boid.acceleration = boid.acceleration.add(alignment).add(cohesion).add(separation).add(edges).add(leader).add(collisionAvoidance);

            // Apply boid acceleration
            var timeScale = gameData.getDeltaTime() / 0.016f;
            boid.position = boid.position.add(boid.velocity.multiply(timeScale));
            boid.velocity = boid.velocity.add(boid.acceleration.multiply(timeScale));
            boid.velocity = boid.velocity.limit(this.maxSpeed);
            boid.acceleration.multiply(0);

            // Sync boid position
            boidPosition.setX(boid.position.x);
            boidPosition.setY(boid.position.y);
            boidPosition.setRadians((float) Math.atan2(boid.velocity.y, boid.velocity.x));
        }

        // If the boid get too far away from the leader, respawn it
        for (var boidEntity : world.getEntitiesWithComponent(Boid.class)) {
            Boid boid = boidEntity.getComponent(Boid.class);
            var leaderPosition = boid.leader.getComponent(Position.class).asVector();

            // Calculate distance in toroidal space
            float dx = Math.abs(boid.position.x - leaderPosition.x);
            float dy = Math.abs(boid.position.y - leaderPosition.y);

            if (dx > gameData.getDisplayWidth() * 0.5f)
                dx = gameData.getDisplayWidth() - dx;

            if (dy > gameData.getDisplayHeight() * 0.5f)
                dy = gameData.getDisplayHeight() * 0.5f - dy;

            float leaderDistance = (float) Math.sqrt(dx * dx + dy * dy);
            if (leaderDistance > 500) {
                world.removeEntity(boidEntity);
                gameData.addEvent(new SpawnAttackships(boid.leader, world, 1));
            }
        }

        // Fire weapons
        float firingRate = 0.01f;
        Random random = new Random();
        for (var entity : world.getEntities(Attackship.class)) {
            if (random.nextFloat() < firingRate) {
                var weapon = entity.getComponent(Weapon.class);
                weapon.changeWeapon();
                weapon.fire(entity, gameData, world);
            }
        }
    }

    private Vector2 align(Boid boid, World world) {
        Vector2 steering = new Vector2();
        int total = 0;
        for (var otherBoid : getBoidsWithinRange(boid, world, this.alignmentRadius)) {
            steering = steering.add(otherBoid.velocity);
            total++;
        }

        if (total > 0) {
            steering = steering.divide(total);
            steering = steering.withLength(this.maxSpeed);
            steering = steering.subtract(boid.velocity);
            steering = steering.limit(this.maxForce);
        }

        return steering;
    }

    private Vector2 cohesion(Boid boid, World world) {
        Vector2 steering = new Vector2();
        int total = 0;
        for (var otherBoid : getBoidsWithinRange(boid, world, this.cohesionRadius)) {
            steering = steering.add(otherBoid.position);
            total++;
        }
        if (total > 0) {
            steering = steering.divide(total);
            steering = steering.subtract(boid.position);
            steering = steering.withLength(this.maxSpeed);
            steering = steering.subtract(boid.velocity);
            steering = steering.limit(this.maxForce);
        }
        return steering;
    }

    private Vector2 separation(Boid boid, World world) {
        Vector2 steering = new Vector2();
        int total = 0;
        for (var otherBoid : getBoidsWithinRange(boid, world, this.separationRadius)) {
            Vector2 diff = boid.position.subtract(otherBoid.position);
            diff = diff.divide(diff.length() * diff.length());
            steering = steering.add(diff);
            total++;
        }
        if (total > 0) {
            steering = steering.divide(total);
            steering = steering.withLength(this.maxSpeed);
            steering = steering.subtract(boid.velocity);
            steering = steering.limit(this.maxForce);
        }

        return steering;
    }

    private Vector2 edges(Boid boid, GameData gameData) {
        Vector2 v = new Vector2();

        if (boid.position.x < 0) {
            v.x = 1;
        } else if (boid.position.x > gameData.getDisplayWidth()) {
            v.x = -1;
        }
        if (boid.position.y < 0) {
            v.y = 1;
        } else if (boid.position.y > gameData.getDisplayHeight()) {
            v.y = -1;
        }

        return v;
    }

    private Vector2 leader(Boid boid, World world) {
        Position leaderPositionComponent = boid.leader.getComponent(Position.class);
        Vector2 leader = new Vector2(leaderPositionComponent.getX(), leaderPositionComponent.getY());

        // Make vector that points from boid to leader
        Vector2 steering = leader.subtract(boid.position);

        if (steering.length() < this.leaderDistance) {
            return new Vector2();
        }

        float excessDistance = steering.length() - this.leaderDistance;


        steering = steering.withLength(this.maxSpeed);
        steering = steering.subtract(boid.velocity);
        steering = steering.limit(this.maxForce);

        // An attempt at making the leader attraction force a gradient
        float leaderGradientFactor = 0.01f;
        steering = steering.multiply(excessDistance * leaderGradientFactor);


        return steering;
    }

    private Vector2 collisionAvoidance(Boid boid, World world) {
        // Only consider collidables within this distance
        float collisionAvoidanceDistance = 2;

        Vector2 steering = new Vector2();
        int total = 0;
        for (Entity collidable : worldCollidables) {
            var collider = collidable.getComponent(Hitbox.class);
            var position = collidable.getComponent(Position.class);
            var hitboxRadius = Math.max(collider.getHeight(), collider.getWidth()) * Math.sqrt(2);
            var hitboxPosition = position.asVector();

            if (boid.position.subtract(hitboxPosition).length() < collisionAvoidanceDistance + hitboxRadius) {
                Vector2 diff = boid.position.subtract(hitboxPosition);
                diff = diff.divide(diff.length() * diff.length());
                steering = steering.add(diff);
                total++;
            }
        }

        if (total > 0) {
            steering = steering.divide(total);
            steering = steering.withLength(this.maxSpeed);
            steering = steering.subtract(boid.velocity);
            steering = steering.limit(this.maxForce * 5);
        }

        return steering;
    }

    private List<Boid> getBoidsWithinRange(Boid boid, World world, float range) {
        return boids.ballSearch(new double[]{boid.position.x, boid.position.y}, range).stream().map(b -> b.getComponent(Boid.class)).filter(b -> !b.equals(boid)).collect(Collectors.toList());
    }

    private void debugMenu() {
        ImGui.begin("Boids");

        maxForce = floatSlider(maxForce, 0, 0.5f, "maxForce");
        maxSpeed = floatSlider(maxSpeed, 0, 5f, "maxSpeed");

        alignmentFactor = floatSlider(alignmentFactor, 0, 1f, "alignmentFactor");
        alignmentRadius = floatSlider(alignmentRadius, 0, 100, "alignmentRadius");

        separationFactor = floatSlider(separationFactor, 0, 5f, "separationFactor");
        separationRadius = floatSlider(separationRadius, 0, 100f, "separationRadius");

        cohesionFactor = floatSlider(cohesionFactor, 0, 0.5f, "cohesionFactor");
        cohesionRadius = floatSlider(cohesionRadius, 0, 100f, "cohesionRadius");

        edgesFactor = floatSlider(edgesFactor, 0, 5f, "edgesFactor");

        leaderFactor = floatSlider(leaderFactor, 0, 5f, "leaderFactor");
        leaderDistance = floatSlider(leaderDistance, 0, 100f, "leaderDistance");

        collisionAvoidanceFactor = floatSlider(collisionAvoidanceFactor, 0, 100f, "collisionAvoidanceFactor");

        ImGui.end();
    }
}
