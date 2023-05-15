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

    private int lastFire;// To make sure the player can shoot as the game starts

    private boolean weapon_changed = false;

    public Weapon(List<IWeapon> weapons){
        lastFire = -1000;
        this.weapons = weapons;
        try {
            weapon = weapons.get(index);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("No weapon implementations found");
        }

    }

    public void changeWeapon(){
        //Get the weapon from weapons with modulus
        if(weapons.size() == 0){
            System.out.println("No weapons available");
            return;
        }

        if(!weapon_changed && swap_cooldown < 0) {
            index++;
            weapon = weapons.get(index % weapons.size());
            weapon_changed = true;
            swap_cooldown = 200;
        }
    }

    public void fire(Entity source, GameData gameData, World world) {
        if(!canFire(gameData)) return;

        weapon.fire(source, world);
        weapon_changed = false;
        lastFire = gameData.getRenderCycles();
    }

    public boolean canFire(GameData gameData){
        int gameTime = gameData.getRenderCycles();
        if (weapon == null) {
            return false;
        }

       if (lastFire + weapon.getReloadTime() > gameTime) {
           return false;
       }
       return true;
    }

    public void decreaseSwapCooldown(){
        swap_cooldown--;
        if (swap_cooldown < 0){
            swap_cooldown = -1;
        }
    }
}
