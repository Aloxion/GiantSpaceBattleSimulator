package gsbs.common.components;

import gsbs.common.services.IWeapon;

public class Weapon extends Component {
    private IWeapon weapon;

    public Weapon(IWeapon weapon){
        this.weapon = weapon;
    }

    public IWeapon getWeapon() {
        return weapon;
    }

    public void fire() {
        if (weapon != null) {
            weapon.fire();
        } else {
            System.out.println("No weapon available");
        }
    }

}
