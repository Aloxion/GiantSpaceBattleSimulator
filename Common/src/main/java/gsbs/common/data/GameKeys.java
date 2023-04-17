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
     */
    public static class Keys {

        //PLAYER MOVEMENT
        public static final int UP = 51; //W
        public static final int LEFT = 29; //A
        public static final int DOWN = 47; //S
        public static final int RIGHT = 32; //D

        // PLAYER CONTROL KEYS
        public static final int WEAPON_CYCLE_UP = 45; //Q
        public static final int WEAPON_CYCLE_DOWN = 33; //E

        public static final int RELEASE_SHIPS = 46; //R
        public static final int SHIPS_DEFEND = 34; //F

        public static final int Z = 54;
        public static final int X = 52;
        public static final int C = 31;
        public static final int V = 50;

        // SYSTEM KEYS
        public static final int ENTER = 66;
        public static final int ESCAPE = 111;
        public static final int SPACE = 62;
        public static final int SHIFT = 59;
    }
}
