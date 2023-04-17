package gsbs.common.events;

import gsbs.common.entities.Entity;

import java.io.Serializable;

public abstract class Event implements Serializable {

    private final Entity source;
    protected EventType eventType;

    public Event(Entity source) {
        this.source = source;
    }

    public Entity getSource() {
        return source;
    }


    public EventType getEventType(){
        return eventType;
    }
}

