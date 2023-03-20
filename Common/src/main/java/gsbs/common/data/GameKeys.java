package gsbs.common.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Used to check the state of a given key
 */
public class GameKeys {
    private static Map<Integer, Boolean> keys;
    private static Map<Integer, Boolean> p_keys;

    public GameKeys() {
        keys = new HashMap<>();
        p_keys = new HashMap<>();
    }

    /**
     * Needs to be called on every frame, in order to update the key states
     */
    public void update() {
        p_keys.replaceAll((k, v) -> keys.get(k));
    }

    /**
     * Set the key state for any specific key
     */
    public void setKey(int k, boolean b) {
        keys.put(k, b);
    }

    /**
     * Get the key down state for any key
     */
    public boolean isDown(int k) {
        if (!keys.containsKey(k))
            return false;

        return keys.get(k);
    }

    /**
     * Check if the key was just pressed, is only true for one frame
     */
    public boolean isPressed(int k) {
        if (!keys.containsKey(k) || !p_keys.containsKey(k))
            return false;

        return keys.get(k) && !p_keys.get(k);
    }

    /**
     * Map the keys over to Core library
     */
    public static class Keys {
        public static final int UP = 19;
        public static final int LEFT = 21;
        public static final int DOWN = 20;
        public static final int RIGHT = 22;
        public static final int ENTER = 66;
        public static final int ESCAPE = 111;
        public static final int SPACE = 62;
        public static final int SHIFT = 59;
    }
}
