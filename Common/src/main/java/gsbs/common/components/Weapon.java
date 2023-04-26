package gsbs.common.components;

import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.entities.Entity;
import gsbs.common.services.IWeapon;

public class Weapon extends Component {
    private IWeapon weapon;

    public Weapon(IWeapon weapon){
        this.weapon = weapon;
    }

    public void fire(Entity source, GameData gameData, World world) {
        if (weapon != null) {
            weapon.fire(source, gameData, world);
        } else {
            System.out.println("No weapon available");
        }
    }
}
