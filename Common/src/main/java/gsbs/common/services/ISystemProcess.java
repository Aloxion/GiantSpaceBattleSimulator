package gsbs.common.services;

import gsbs.common.data.GameData;
import gsbs.common.data.World;

/**
 * This will be called on every frame, no matter what the game state is
 */
public interface ISystemProcess {
    void process(GameData gameData, World world);
}
