package gsbs.weapon.implementations;

import gsbs.common.services.IWeapon;

public class Pistol implements IWeapon {

    @Override
    public void fire() {
        System.out.println("FIRE MY PISTOL");
    }
}
