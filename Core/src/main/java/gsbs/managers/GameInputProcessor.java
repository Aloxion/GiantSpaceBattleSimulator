package gsbs.managers;

import gsbs.common.data.GameData;
import gsbs.common.events.EventManager;
import gsbs.common.events.MouseClickEvent;
import gsbs.common.events.PlayerControlEvent;


public class GameInputProcessor {
    private final GameData gameData;
    private final EventManager eventManager;

    public GameInputProcessor(GameData gameData, EventManager eventManager) {
        this.gameData = gameData;
        this.eventManager = eventManager;
    }

    public boolean keyDown(int key) {
        eventManager.addEvent(new PlayerControlEvent(null, key, true));
        return true;
    }

    public boolean keyUp(int key) {
        eventManager.addEvent(new PlayerControlEvent(null, key, false));
        return true;
    }

    public boolean TouchDown(int screenX, int screenY, int pointer, int button) {
        eventManager.addEvent(new MouseClickEvent(null, screenX, screenY, button));
        return true;
    }
}


