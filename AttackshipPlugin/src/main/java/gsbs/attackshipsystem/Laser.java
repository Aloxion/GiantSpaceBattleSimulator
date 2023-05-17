package gsbs.attackshipsystem;

import gsbs.common.components.*;
import gsbs.common.data.World;
import gsbs.common.entities.Bullet;
import gsbs.common.entities.Entity;
import gsbs.common.services.IWeapon;

public class Laser implements IWeapon {
    private final int duration = 25;
    private final String laserSprite;

    public Laser(String laserSprite) {
        this.laserSprite = laserSprite;
    }

    @Override
    public void fire(Entity entity, World world) {
        Position startPosition = entity.getComponent(Position.class);

        float acceleration = 10000;
        float deacceleration = 20;
        float maxSpeed = 300;
        float rotationSpeed = 0;

        Entity bullet = new Bullet();
        bullet.add(new Movement(deacceleration, acceleration, maxSpeed, rotationSpeed));
        Sprite sprite = new Sprite(Laser.class.getResource(this.laserSprite), 15, 20);
        bullet.add(new Position(startPosition.getX() + (sprite.getWidth() / 2.f), startPosition.getY() + (sprite.getHeight() / 2.f), startPosition.getRadians()));
        bullet.add(sprite);
        bullet.add(new Hitbox(sprite.getWidth(), sprite.getHeight(), startPosition.getX() + (sprite.getWidth() / 2.f), startPosition.getY() + (sprite.getHeight() / 2.f)));
        bullet.add(new Team(entity.getComponent(Team.class).getTeam()));

        bullet.add(new Health(duration));

        world.addEntity(bullet);

    }

    @Override
    public int getReloadTime() {
        return duration;
    }
}
