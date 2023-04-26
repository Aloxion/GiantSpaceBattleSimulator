package gsbs.common.services;

import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.entities.Entity;

public interface IWeapon {
    void fire(Entity entity, GameData gameData, World world);
}
