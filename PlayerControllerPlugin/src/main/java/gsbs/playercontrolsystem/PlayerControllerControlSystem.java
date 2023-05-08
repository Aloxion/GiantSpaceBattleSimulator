package gsbs.playercontrolsystem;


import gsbs.common.components.Movement;
import gsbs.common.components.Team;
import gsbs.common.components.Weapon;
import gsbs.common.data.GameData;
import gsbs.common.data.GameKeys;
import gsbs.common.data.World;
import gsbs.common.data.enums.Teams;
import gsbs.common.entities.Entity;
import gsbs.common.entities.Flagship;
import gsbs.common.events.Event;
import gsbs.common.events.EventType;
import gsbs.common.events.PlayerControlEvent;
import gsbs.common.services.IEventListener;
import gsbs.common.services.IProcess;

public class PlayerControllerControlSystem implements IProcess, IEventListener {
    @Override
    public void process(GameData gameData, World world) {
        for (Entity player : world.getEntities(Flagship.class)) {
            var team = player.getComponent(Team.class);
            if (team.getTeam() == Teams.PLAYER){

                var movement = player.getComponent(Movement.class);
                movement.setLeft(gameData.getKeys().isDown(GameKeys.Keys.LEFT));
                movement.setRight(gameData.getKeys().isDown(GameKeys.Keys.RIGHT));
                movement.setUp(gameData.getKeys().isDown(GameKeys.Keys.UP));

                var weapon = player.getComponent(Weapon.class);
                if (gameData.getKeys().isDown(GameKeys.Keys.SPACE)){
                    weapon.fire(player, gameData, world);
                }

                if (gameData.getKeys().isDown(GameKeys.Keys.WEAPON_CYCLE_UP)){
                    weapon.changeWeapon();
                }

                if (gameData.getKeys().isDown(GameKeys.Keys.WEAPON_CYCLE_DOWN)){
                    weapon.changeWeapon();
                }

            }
        }
    }

    @Override
    public void onEvent(Event event, GameData gameData) {
        if (event.getEventType() == EventType.PLAYER_CONTROL) {
            PlayerControlEvent controlEvent = (PlayerControlEvent) event;
            gameData.getKeys().setKey(controlEvent.getKeyCode(), controlEvent.isKeyPressed());
        }
    }
}