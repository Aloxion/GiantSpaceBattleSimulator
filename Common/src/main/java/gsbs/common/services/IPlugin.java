package gsbs.common.services;

import gsbs.common.data.GameData;
import gsbs.common.data.World;

public interface IPlugin {

    /**
     * This will be called when the plugin is initialized
     */
    void start(GameData gameData, World world);

    /**
     * This will be called when the plugin is unloaded or the game stops.
     */
    void stop(GameData gameData, World world);
}
