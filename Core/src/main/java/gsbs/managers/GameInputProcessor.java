package gsbs.managers;

import com.badlogic.gdx.InputAdapter;
import gsbs.common.data.GameData;


public class GameInputProcessor extends InputAdapter {
    private final GameData gameData;

    public GameInputProcessor(GameData gameData) {
        this.gameData = gameData;
    }

    public boolean keyDown(int key) {
        gameData.getKeys().setKey(key, true);
        return true;
    }

    public boolean keyUp(int key) {
        gameData.getKeys().setKey(key, false);
        return true;
    }
}


