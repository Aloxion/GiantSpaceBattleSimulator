package gsbs.flagshipsystem;


import gsbs.common.components.Graphics;
import gsbs.common.components.Position;
import gsbs.common.components.Team;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.data.enums.Teams;
import gsbs.common.entities.Entity;
import gsbs.common.entities.Flagship;
import gsbs.common.events.Event;
import gsbs.common.events.EventType;
import gsbs.common.services.IEventListener;
import gsbs.common.events.PlayerControlEvent;
import gsbs.common.math.Vector2;
import gsbs.common.services.IProcess;
import java.util.List;

public class FlagshipAIControlSystem implements IProcess {

    @Override
    public void process(GameData gameData, World world) {
        for (Entity enemyFlagship : world.getEntities(Flagship.class)) {
            var team = enemyFlagship.getComponent(Team.class);
            if (team.getTeam() == Teams.ENEMY) {
                

            }
        }
    }
}