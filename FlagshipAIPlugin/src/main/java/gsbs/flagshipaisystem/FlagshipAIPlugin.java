package gsbs.flagshipaisystem;

import gsbs.common.components.*;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.data.enums.Teams;
import gsbs.common.entities.Entity;
import gsbs.common.entities.FlagshipAI;
import gsbs.common.services.IPlugin;


public class FlagshipAIPlugin implements IPlugin {
    private Entity enemyFlagshipAI;

    @Override
    public void start(GameData gameData, World world) {
        enemyFlagshipAI = createFlagshipAI(gameData, world, Teams.ENEMY);
        world.addEntity(enemyFlagshipAI);
        System.out.println("AI plugin start");
    }

    @Override
    public void stop(GameData gameData, World world) {
        System.out.println("Stop player");
    }


    private Entity createFlagshipAI(GameData gameData, World world, Teams team) {

        Entity enemyFlagshipAI = new FlagshipAI();
        enemyFlagshipAI.add(new Team(team));

        return enemyFlagshipAI;
    }
}

