package gsbs.common.components;

import gsbs.common.data.GameData;
import gsbs.common.entities.Entity;
import gsbs.common.services.IWeapon;

public class Weapon extends Component {
    private IWeapon weapon;

    public Weapon(IWeapon weapon){
        this.weapon = weapon;
    }

    public void fire(Entity source, GameData gameData) {
        if (weapon != null) {
            weapon.fire(source, gameData);
        } else {
            System.out.println("No weapon available");
        }
    }

}
