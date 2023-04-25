package gsbs.weapon.implementations;

import gsbs.common.components.*;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.entities.Bullet;
import gsbs.common.entities.Entity;
import gsbs.common.services.IWeapon;
import gsbs.weapon.BulletControlSystem;

public class Pistol implements IWeapon {

    @Override
    public void fire(Entity source, GameData gameData, World world) {
        Position startPosition = source.getComponent(Position.class);
        world.addEntity(BulletControlSystem.createBullet(startPosition));
    }
}
