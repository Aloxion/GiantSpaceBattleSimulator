package gsbs.playercontrolsystem;


import gsbs.common.components.Graphics;
import gsbs.common.components.Movement;
import gsbs.common.components.Position;
import gsbs.common.components.Team;
import gsbs.common.data.GameData;
import gsbs.common.data.GameKeys;
import gsbs.common.data.World;
import gsbs.common.entities.Entity;
import gsbs.common.entities.Flagship;
import gsbs.common.math.Vector2;
import gsbs.common.services.IProcess;
import java.util.List;

public class PlayerControllerControlSystem implements IProcess {


    @Override
    public void process(GameData gameData, World world) {
        for (Entity player : world.getEntities(Flagship.class)) {
            var team = player.getComponent(Team.class);
            if (team.getTeamNumber() == 1){
                var movement = player.getComponent(Movement.class);
                movement.setLeft(gameData.getKeys().isDown(GameKeys.Keys.LEFT));
                movement.setRight(gameData.getKeys().isDown(GameKeys.Keys.RIGHT));
                movement.setUp(gameData.getKeys().isDown(GameKeys.Keys.UP));
            }
        }
    }
}