package gsbs.common.data.enums;

import java.util.Random;

public enum AsteroidSizes {
    Small(1, 128 / 3),
    Medium(2, 128 / 2),
    Large(3, 128);
    private final float size;
    AsteroidSizes(float type, float size) {
        this.size = size;
    }
    public float getSize() {
        return this.size;
    }
}
