package gsbs.playercontrolsystem;

import gsbs.common.data.GameData;
import gsbs.common.data.GameKeys;
import gsbs.common.data.World;
import gsbs.common.entities.Entity;
import gsbs.common.events.EventType;
import gsbs.common.events.PlayerControlEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerControllerControlSystemTest {
    private PlayerControllerControlSystem systemUnderTest;
    private GameData gameData;
    private World world;

    @BeforeEach
    void setUp() {
        long nvgContext = 0L;
        systemUnderTest = new PlayerControllerControlSystem();
        gameData = new GameData(nvgContext);
        world = new World();
    }

    @Test
    void testProcess_keyLeftDown() {
        gameData.getKeys().setKey(GameKeys.Keys.LEFT, true);
        systemUnderTest.process(gameData, world);
        assertTrue(gameData.getKeys().isDown(GameKeys.Keys.LEFT));
    }

    @Test
    void testProcess_keyRightDown() {
        gameData.getKeys().setKey(GameKeys.Keys.RIGHT, true);
        systemUnderTest.process(gameData, world);
        assertTrue(gameData.getKeys().isDown(GameKeys.Keys.RIGHT));
    }

    @Test
    void testProcess_keyUpDown() {
        gameData.getKeys().setKey(GameKeys.Keys.UP, true);
        systemUnderTest.process(gameData, world);
        assertTrue(gameData.getKeys().isDown(GameKeys.Keys.UP));
    }

    @Test
    void testProcess_keySpaceDown() {
        gameData.getKeys().setKey(GameKeys.Keys.SPACE, true);
        systemUnderTest.process(gameData, world);
        assertTrue(gameData.getKeys().isDown(GameKeys.Keys.SPACE));
    }

    @Test
    void testProcess_keyWeaponCycleUpDown() {
        gameData.getKeys().setKey(GameKeys.Keys.WEAPON_CYCLE_UP, true);
        systemUnderTest.process(gameData, world);
        assertTrue(gameData.getKeys().isDown(GameKeys.Keys.WEAPON_CYCLE_UP));
    }

    @Test
    void testProcess_keyWeaponCycleDownDown() {
        gameData.getKeys().setKey(GameKeys.Keys.WEAPON_CYCLE_DOWN, true);
        systemUnderTest.process(gameData, world);
        assertTrue(gameData.getKeys().isDown(GameKeys.Keys.WEAPON_CYCLE_DOWN));
    }

}

