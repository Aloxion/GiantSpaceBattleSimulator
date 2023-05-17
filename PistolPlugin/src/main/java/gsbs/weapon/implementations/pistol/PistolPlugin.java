package gsbs.weapon.implementations.pistol;

import gsbs.common.components.*;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.data.enums.Teams;
import gsbs.common.entities.Bullet;
import gsbs.common.entities.Entity;
import gsbs.common.services.IWeapon;

public class PistolPlugin implements IWeapon {
    private final int reloadTime = 25;
    private final int duration = 200;

    @Override
    public void fire(Entity source, World world) {
        addBullet(source, world);
    }

    @Override
    public int getReloadTime() {
        return reloadTime;
    }

    private void addBullet(Entity source, World world){
        Position startPosition = source.getComponent(Position.class);

        world.addEntity(createBullet(source,startPosition, duration));
    }

    private Entity createBullet(Entity source,Position position, int duration){
        float acceleration = 10000;
        float deacceleration = 20;
        float maxSpeed = 300;
        float rotationSpeed = 0;

        Entity bullet = new Bullet();
        bullet.add(new Movement(deacceleration, acceleration, maxSpeed, rotationSpeed));
        Sprite sprite = new Sprite(PistolPlugin.class.getResource("/default-bullet.png"), 10, 10);
        bullet.add(new Position(position.getX() + (sprite.getWidth()/2), position.getY() + (sprite.getHeight()/2), position.getRadians()));
        bullet.add(sprite);
        bullet.add(new Hitbox(sprite.getWidth(),sprite.getHeight(),position.getX() + (sprite.getWidth()/2),position.getY() + (sprite.getHeight()/2)));
        bullet.add(new Team(source.getComponent(Team.class).getTeam()));

        bullet.add(new Health(duration));
        return bullet;
    }

}
