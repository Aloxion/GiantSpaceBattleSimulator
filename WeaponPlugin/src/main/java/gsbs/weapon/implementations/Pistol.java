package gsbs.weapon.implementations;

import gsbs.common.components.*;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.entities.Bullet;
import gsbs.common.entities.Entity;
import gsbs.common.services.IWeapon;
import gsbs.weapon.BulletControlSystem;

public class Pistol implements IWeapon {
    private final int reloadTime = 25;
    private final int duration = 200;
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

        //world.addEntity(BulletControlSystem.createBullet(startPosition, duration));
    }

}
