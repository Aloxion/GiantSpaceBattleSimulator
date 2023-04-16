package gsbs.common.events;

import gsbs.common.entities.Entity;

public class MouseClickEvent extends Event {
    private final int x;
    private final int y;
    private final int button;

    public MouseClickEvent(Entity source, int x, int y, int button) {
        super(source);
        this.eventType = EventType.MOUSE_CLICK;
        this.x = x;
        this.y = y;
        this.button = button;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getButton() {
        return button;
    }
}