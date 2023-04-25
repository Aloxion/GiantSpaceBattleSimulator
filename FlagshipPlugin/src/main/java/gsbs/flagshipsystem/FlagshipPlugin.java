package gsbs.flagshipsystem;

import gsbs.common.components.*;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.data.enums.Teams;
import gsbs.common.entities.Entity;
import gsbs.common.entities.Flagship;
import gsbs.common.services.IPlugin;
import gsbs.common.services.IWeapon;

import java.util.ServiceLoader;

public class FlagshipPlugin implements IPlugin {
    private Entity playerFlagship;
    private Entity enemyFlagship;

    @Override
    public void start(GameData gameData, World world) {
        playerFlagship = createFlagship(gameData, world, Teams.PLAYER, gameData.getDisplayWidth() / 10.0f, gameData.getDisplayHeight() / 2.0f, 0);
        world.addEntity(playerFlagship);

        enemyFlagship = createFlagship(gameData, world, Teams.ENEMY, gameData.getDisplayWidth() - gameData.getDisplayWidth() / 10.0f, gameData.getDisplayHeight() / 2.0f, (float) Math.PI);
        world.addEntity(enemyFlagship);
    }

    @Override
    public void stop(GameData gameData, World world) {
        System.out.println("Stop player");
    }


    private Entity createFlagship(GameData gameData, World world, Teams team, float x, float y, float radians) {
        float deacceleration = 10;
        float acceleration = 12.5f;
        float maxSpeed = 10;
        float rotationSpeed = 0.4f;

        IWeapon weapon = loadWeapon();

        Entity Ship = new Flagship();
        Ship.add(new Graphics());
        Ship.add(new Movement(deacceleration, acceleration, maxSpeed, rotationSpeed));
        Ship.add(new Position(x, y, radians));
        Ship.add(new Team(team));
        Ship.add(new Weapon(weapon));

        return Ship;
    }

    private IWeapon loadWeapon(){
        ServiceLoader<IWeapon> loader = ServiceLoader.load(IWeapon.class);
        if (loader.iterator().hasNext()) {
            return loader.iterator().next();
        }
        return null;
    }
}