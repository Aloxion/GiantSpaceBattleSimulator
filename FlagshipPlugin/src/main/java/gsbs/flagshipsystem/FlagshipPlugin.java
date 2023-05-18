package gsbs.flagshipsystem;

import gsbs.common.components.*;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.data.enums.Teams;
import gsbs.common.entities.Entity;
import gsbs.common.entities.Flagship;
import gsbs.common.events.SpawnAttackships;
import gsbs.common.services.IPlugin;
import gsbs.common.services.IWeapon;
import gsbs.common.util.PluginManager;

import java.util.List;

public class FlagshipPlugin implements IPlugin {
    private Entity playerFlagship;
    private Entity enemyFlagship;

    @Override
    public void start(GameData gameData, World world) {
        playerFlagship = createFlagship(gameData, world, Teams.PLAYER, gameData.getDisplayWidth() / 10.0f, gameData.getDisplayHeight() / 2.0f, 0);
        world.addEntity(playerFlagship);
        gameData.addEvent(new SpawnAttackships(playerFlagship, world, 10));

        enemyFlagship = createFlagship(gameData, world, Teams.ENEMY, gameData.getDisplayWidth() - gameData.getDisplayWidth() / 10.0f, gameData.getDisplayHeight() / 2.0f, (float) Math.PI);
        world.addEntity(enemyFlagship);
        gameData.addEvent(new SpawnAttackships(enemyFlagship, world, 10));
    }

    @Override
    public void stop(GameData gameData, World world) {
        System.out.println("Stop player");
    }


    private Entity createFlagship(GameData gameData, World world, Teams team, float x, float y, float radians) {
        float deacceleration = 10;
        float acceleration = 50f;
        float maxSpeed = 50;
        float rotationSpeed = 0.4f;

        List<IWeapon> weapons = loadWeapons();

        Entity Ship = new Flagship();
        Ship.add(new Health(30));
        Ship.add(new Movement(deacceleration, acceleration, maxSpeed, rotationSpeed));
        Ship.add(new Position(x, y, radians));
        Ship.add(new Sprite(FlagshipPlugin.class.getResource("/flagship.png"), 32, 32));
        Ship.add(new Hitbox(32, 32, x, y));
        Ship.add(new Team(team));
        Ship.add(new Weapon(weapons));

        return Ship;
    }

    private List<IWeapon> loadWeapons() {
        List<IWeapon> loader = PluginManager.locateAll(IWeapon.class);
        System.out.println("Found " + loader.size() + " weapons");
        return loader;
    }
}
