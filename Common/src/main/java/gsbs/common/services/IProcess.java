package gsbs.common.services;

import gsbs.common.data.GameData;
import gsbs.common.data.World;

/**
 * This is the interface for the processor, and will be called on every frame.
 */
public interface IProcess {
    void process(GameData gameData, World world);
}
