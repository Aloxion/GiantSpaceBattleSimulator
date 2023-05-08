package gsbs.weapon.implementations;

import gsbs.common.components.Position;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.entities.Entity;
import gsbs.common.services.IWeapon;
import gsbs.weapon.BulletControlSystem;

public class Shotgun implements IWeapon {
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
            //world.addEntity(BulletControlSystem.createBullet(position, duration));
        }

    }
}
