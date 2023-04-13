package gsbs.managers;

import com.badlogic.gdx.InputAdapter;
import gsbs.common.data.GameData;
import gsbs.common.events.EventManager;
import gsbs.common.events.PlayerControlEvent;


public class GameInputProcessor extends InputAdapter {
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
}


