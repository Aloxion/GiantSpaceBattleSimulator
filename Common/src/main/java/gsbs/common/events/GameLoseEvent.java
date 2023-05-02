package gsbs.common.events;

import gsbs.common.entities.Entity;

public class GameLoseEvent extends Event {
    public GameLoseEvent(Entity source) {
        super(source);
    }
}
