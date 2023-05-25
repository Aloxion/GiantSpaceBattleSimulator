package gsbs.common.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Used to check the state of a given key
 */
public class GameKeys {
    private static Map<Integer, Boolean> keys;
    private static Map<Integer, Boolean> previous_keys;

    public GameKeys() {
        keys = new HashMap<>();
        previous_keys = new HashMap<>();
    }

    /**
     * Needs to be called on every frame, in order to update the key states
     */
    public void update() {
        previous_keys.replaceAll((k, v) -> keys.get(k));
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
     * Map the keys over to Core library
     * <a href="https://www.glfw.org/docs/3.3/group__keys.html">...</a>
     */
    public static class Keys {
        //PLAYER MOVEMENT
        public static final int UP = 265;
        public static final int LEFT = 262;
        public static final int DOWN = 264;
        public static final int RIGHT = 263;

        // PLAYER CONTROL KEYS
        public static final int WEAPON_CYCLE_UP = 81; //Q
        public static final int WEAPON_CYCLE_DOWN = 69; //E
        public static final int SPACE = 32;

    }
}
