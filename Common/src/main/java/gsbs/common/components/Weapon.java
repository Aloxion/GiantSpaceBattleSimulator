package gsbs.common.components;

import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.entities.Entity;
import gsbs.common.services.IWeapon;

import java.util.List;

public class Weapon extends Component {

    private List<IWeapon> weapons;
    private IWeapon weapon;

    private int index = 0;
    private int swap_cooldown = 100;

    private boolean weapon_changed = false;

    public Weapon(List<IWeapon> weapons){
        this.weapons = weapons;
        try {
            weapon = weapons.get(index);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("No weapon implementations found");
        }

    }

    public void changeWeapon(){
        //Get the weapon from weapons with modulus
        if(!weapon_changed && swap_cooldown < 0) {
            index++;
            weapon = weapons.get(index % weapons.size());
            weapon_changed = true;
            swap_cooldown = 200;
        }
    }

    public void fire(Entity source, GameData gameData, World world) {
        if (weapon != null) {
            weapon.fire(source, gameData, world);
            weapon_changed = false;
        } else {
            System.out.println("No weapon available");
        }
    }

    public void decreaseSwapCooldown(){
        swap_cooldown--;
        if (swap_cooldown < 0){
            swap_cooldown = -1;
        }
    }
}
