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

        Entity ship = new Flagship();
        ship.add(new Health(30));
        ship.add(new Movement(deacceleration, acceleration, maxSpeed, rotationSpeed));
        ship.add(new Position(x, y, radians));
        ship.add(new Team(team));
        ship.add(new Weapon(weapons));
        Sprite sprite = null;
        switch (team) {
            case PLAYER:
                sprite = new Sprite(getClass().getResource("/PlayerFlagship.png"), 70, 70);
                break;
            case ENEMY:
                sprite = new Sprite(getClass().getResource("/EnemyFlagship.png"), 70, 70);
                break;
        }
        ship.add(new Hitbox(sprite.getWidth()/2f, sprite.getHeight()/2f, x, y));
        ship.add(sprite);
        return ship;
    }

    private List<IWeapon> loadWeapons() {
        List<IWeapon> loader = PluginManager.locateAll(IWeapon.class);
        System.out.println("Found " + loader.size() + " weapons");
        return loader;
    }
}
