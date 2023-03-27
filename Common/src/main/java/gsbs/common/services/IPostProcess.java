package gsbs.common.services;

import gsbs.common.data.GameData;
import gsbs.common.data.World;

/**
 * This is like the IProcess, but will be called when all the normal processors have been run.
 */
public interface IPostProcess {
    void process(GameData gameData, World world);
}
