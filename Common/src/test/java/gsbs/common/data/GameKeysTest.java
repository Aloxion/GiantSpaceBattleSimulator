package gsbs.common.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameKeysTest {

    private GameKeys gameKeys;

    @BeforeEach
    public void setUp() {
        gameKeys = new GameKeys();
    }

    @Test
    public void testKeyDown() {
        gameKeys.setKey(GameKeys.Keys.UP, true);

        assertTrue(gameKeys.isDown(GameKeys.Keys.UP));
        assertFalse(gameKeys.isDown(GameKeys.Keys.DOWN));
    }

    @Test
    public void testKeyUp() {
        gameKeys.setKey(GameKeys.Keys.UP, true); //Simulate keydown event
        gameKeys.setKey(GameKeys.Keys.UP, false);

        assertFalse(gameKeys.isDown(GameKeys.Keys.UP));
    }
}