package gsbs.common.events;

import gsbs.common.data.World;
import gsbs.common.data.enums.Teams;
import gsbs.common.entities.Entity;

public class SpawnCarrier extends Event {
    private final World world;
    private final Teams team;

    public SpawnCarrier(Entity source, World world, Teams team) {
        super(source);
        this.world = world;
        this.team = team;
    }

    public World getWorld() {
        return world;
    }

    public Teams getTeam() {
        return team;
    }
}
