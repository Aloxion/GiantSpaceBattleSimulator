package gsbs.common.events;

import gsbs.common.entities.Entity;

public class GameWinEvent extends Event {
    public GameWinEvent(Entity source) {
        super(source);
    }
}
