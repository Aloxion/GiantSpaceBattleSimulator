package gsbs.carriersystem;

import gsbs.common.components.*;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.data.enums.Teams;
import gsbs.common.entities.Carrier;
import gsbs.common.entities.Entity;
import gsbs.common.events.SpawnAttackships;
import gsbs.common.services.IPlugin;
import gsbs.common.services.IWeapon;
import gsbs.common.util.PluginManager;

import java.util.ArrayList;
import java.util.List;

public class CarrierPlugin implements IPlugin {
    private final int maxCarriersInOneTeam = 10;
    private final int maxAttackshipsPerCarrier = 30;
    private final int maxHealthForCarrier = 50;
    private List<Entity> playerCarriers;
    private List<Entity> enemyCarriers;

    @Override
    public void start(GameData gameData, World world) {
        playerCarriers = new ArrayList<Entity>();
        enemyCarriers = new ArrayList<Entity>();

        for (int i = 1; i < maxCarriersInOneTeam + 1; i++) {
            float ySpawn = gameData.getDisplayHeight() / (float) (maxCarriersInOneTeam + 1);
            playerCarriers.add(createCarrier(gameData, world, Teams.PLAYER, gameData.getDisplayWidth() / 12.0f, ySpawn * i, 0));
        }

        for (Entity carrier : playerCarriers) {
            world.addEntity(carrier);
            gameData.addEvent(new SpawnAttackships(carrier, world, maxAttackshipsPerCarrier));
        }

        for (int i = 1; i < maxCarriersInOneTeam + 1; i++) {
            float ySpawn = gameData.getDisplayHeight() / (float) (maxCarriersInOneTeam + 1);
            enemyCarriers.add(createCarrier(gameData, world, Teams.ENEMY, gameData.getDisplayWidth() - gameData.getDisplayWidth() / 12.0f, ySpawn * i, (float) Math.PI));
        }

        for (Entity carrier : enemyCarriers) {
            world.addEntity(carrier);
            gameData.addEvent(new SpawnAttackships(carrier, world, maxAttackshipsPerCarrier));
        }
    }

    @Override
    public void stop(GameData gameData, World world) {
        System.out.println("Stop player");
    }


    private Entity createCarrier(GameData gameData, World world, Teams team, float x, float y, float radians) {
        float deacceleration = 10;
        float acceleration = 50f;
        float maxSpeed = 50;
        float rotationSpeed = 0.4f;

        List<IWeapon> weapons = loadWeapons();

        Entity Ship = new Carrier();
        Ship.add(new Health(maxHealthForCarrier));
        Ship.add(new Movement(deacceleration, acceleration, maxSpeed, rotationSpeed));
        Ship.add(new Position(x, y, radians));
        Ship.add(new Sprite(CarrierPlugin.class.getResource("/flagship.png"), 50, 50));
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
