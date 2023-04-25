package gsbs.weapon.implementations;

import gsbs.common.data.GameData;
import gsbs.common.entities.Entity;
import gsbs.common.services.IWeapon;

public class Pistol implements IWeapon {

    @Override
    public void fire(Entity source, GameData gameData) {
        System.out.println("FIRE MY PISTOL");
    }
}
