package gsbs.common.services;

import gsbs.common.data.GameData;
import gsbs.common.data.World;

/**
 * This is the interface for the processor, and will be called on every frame when the game state is In-Game.
 */
public interface IProcess {
    void process(GameData gameData, World world);
}
