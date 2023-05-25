package gsbs.common.events;

import gsbs.common.data.World;
import gsbs.common.entities.Entity;

public class SpawnAttackships extends Event {
    private final World world;
    private final int amount;

    public SpawnAttackships(Entity source, World world, int amount) {
        super(source);
        this.world = world;
        this.amount = amount;
    }

    public World getWorld() {
        return world;
    }

    public int getAmount() {
        return amount;
    }
}
