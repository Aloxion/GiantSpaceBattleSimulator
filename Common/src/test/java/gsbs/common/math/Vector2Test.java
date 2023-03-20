package gsbs.common.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Vector2Test {

    @Test
    void testEquals() {
        var vector0 = new Vector2();
        var vector1 = new Vector2(1,1);
        var vector2 = new Vector2(1,1);

        assertTrue(vector1.equals(vector2));
        assertFalse(vector0.equals(vector1));
    }
}