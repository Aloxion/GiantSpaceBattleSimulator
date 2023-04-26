package gsbs.playercontrolsystem;

import gsbs.common.components.Graphics;
import gsbs.common.components.Movement;
import gsbs.common.components.Position;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.entities.Entity;
import gsbs.common.entities.PlayerController;
import gsbs.common.services.IPlugin;

public class PlayerControllerPlugin implements IPlugin {
    private Entity playerController;


    @Override
    public void start(GameData gameData, World world) {
        playerController = createPlayerController(gameData, world);
        world.addEntity(playerController);
    }

    @Override
    public void stop(GameData gameData, World world) {
        System.out.println("Stop player");
    }


    private Entity createPlayerController(GameData gameData, World world) {

        Entity controller = new PlayerController();

        return controller;
    }
}
