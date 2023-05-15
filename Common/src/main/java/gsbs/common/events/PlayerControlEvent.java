package gsbs.common.events;

import gsbs.common.entities.Entity;

public class PlayerControlEvent extends Event {
    private final int keyCode;
    private final boolean keyPressed;

    public PlayerControlEvent(Entity source, int keyCode, boolean keyPressed) {
        super(source);
        this.eventType = EventType.PLAYER_CONTROL;
        this.keyCode = keyCode;
        this.keyPressed = keyPressed;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public boolean isKeyPressed() {
        return keyPressed;
    }
}
