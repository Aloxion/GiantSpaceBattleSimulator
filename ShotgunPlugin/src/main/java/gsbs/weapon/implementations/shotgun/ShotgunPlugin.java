package gsbs.weapon.implementations.shotgun;

import gsbs.common.components.*;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.data.enums.Teams;
import gsbs.common.entities.Bullet;
import gsbs.common.entities.Entity;
import gsbs.common.services.IWeapon;

public class ShotgunPlugin implements IWeapon {
    private final int reloadTime = 100;
    private final int duration = 50;

    private int lastFire = -reloadTime; // To make sure the player can shoot as the game starts


    @Override
    public void fire(Entity source, GameData gameData, World world) {
        int gameTime = gameData.getRenderCycles();
        if(lastFire + reloadTime < gameTime){

            addBullet(source, world);
            lastFire = gameTime;
        }
    }

    private void addBullet(Entity source, World world){
        Position startPosition = source.getComponent(Position.class);

        //Create bullets in a semicircle
        for(int i = -2; i < 3; i++){
            float radians = (float) (startPosition.getRadians() + i * Math.PI / 6);
            Position position = new Position(startPosition.getX(), startPosition.getY(), radians);
            world.addEntity(createBullet(source, position, duration));
        }

    }


    private Entity createBullet(Entity source,Position position, int duration){
        float acceleration = 10000;
        float deacceleration = 20;
        float maxSpeed = 300;
        float rotationSpeed = 0;

        Entity bullet = new Bullet();
        Sprite sprite = new Sprite(ShotgunPlugin.class.getResource("/default-bullet.png"), 10, 10);

        bullet.add(new Movement(deacceleration, acceleration, maxSpeed, rotationSpeed));
        bullet.add(new Position(position.getX() + (sprite.getWidth()/2), position.getY() + (sprite.getHeight()/2), position.getRadians()));
        bullet.add(sprite);
        bullet.add(new Hitbox(sprite.getWidth(),sprite.getHeight(),position.getX() + (sprite.getWidth()/2),position.getY() + (sprite.getHeight()/2)));
        bullet.add(new Team(source.getComponent(Team.class).getTeam()));

        bullet.add(new Health(duration));
        return bullet;
    }

}
