package gsbs.common.data.enums;

import java.util.Random;

public enum AsteroidSizes {
    Small(1, 128 / 4),
    Medium(2, 128 / 2),
    Large(3, 128);

    private static final Random getRandom = new Random();
    private final int size;

    AsteroidSizes(int type, int size) {
        this.size = size;
    }

    public static AsteroidSizes randomDirection() {
        AsteroidSizes[] asteroids = values();
        return asteroids[getRandom.nextInt(asteroids.length)];
    }

    public int getSize() {
        return this.size;
    }
}
