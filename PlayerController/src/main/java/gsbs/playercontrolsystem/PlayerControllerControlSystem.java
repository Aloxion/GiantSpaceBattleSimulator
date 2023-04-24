package gsbs.playercontrolsystem;


import gsbs.common.components.Movement;
import gsbs.common.components.Team;
import gsbs.common.data.GameData;
import gsbs.common.data.GameKeys;
import gsbs.common.data.World;
import gsbs.common.data.enums.Teams;
import gsbs.common.entities.Entity;
import gsbs.common.entities.Flagship;
import gsbs.common.services.IProcess;

public class PlayerControllerControlSystem implements IProcess {


    @Override
    public void process(GameData gameData, World world) {
        for (Entity player : world.getEntities(Flagship.class)) {
            var team = player.getComponent(Team.class);
            if (team.getTeam() == Teams.PLAYER){

                var movement = player.getComponent(Movement.class);
                movement.setLeft(gameData.getKeys().isDown(GameKeys.Keys.LEFT));
                movement.setRight(gameData.getKeys().isDown(GameKeys.Keys.RIGHT));
                movement.setUp(gameData.getKeys().isDown(GameKeys.Keys.UP));
            }
        }
    }
}