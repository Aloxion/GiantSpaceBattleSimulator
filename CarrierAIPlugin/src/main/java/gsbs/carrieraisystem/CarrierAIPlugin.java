package gsbs.carrieraisystem;

import gsbs.common.components.*;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.data.enums.Teams;
import gsbs.common.entities.CarrierAI;
import gsbs.common.entities.Entity;
import gsbs.common.entities.FlagshipAI;
import gsbs.common.services.IPlugin;


public class CarrierAIPlugin implements IPlugin {
    private Entity enemyCarrierAI;
    private Entity playerCarrierAI;

    @Override
    public void start(GameData gameData, World world) {
        /*enemyCarrierAI = createCarrierAI(gameData, world, Teams.ENEMY);
        world.addEntity(enemyCarrierAI);

        playerCarrierAI = createCarrierAI(gameData, world, Teams.PLAYER);
        world.addEntity(playerCarrierAI);*/
    }

    @Override
    public void stop(GameData gameData, World world) {
        System.out.println("Stop player");
    }


    private Entity createCarrierAI(GameData gameData, World world, Teams team) {

        Entity carrierAI = new CarrierAI();
        carrierAI.add(new Team(team));

        return carrierAI;
    }
}

