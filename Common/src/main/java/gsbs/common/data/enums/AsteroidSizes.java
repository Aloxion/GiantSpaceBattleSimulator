package gsbs.common.data.enums;

import java.util.Random;

public enum AsteroidSizes {
    Small(1, 256/4),
    Medium(2, 256/2),
    Large(3, 256);

    private final int size;
    private static final Random getRandom = new Random();

    AsteroidSizes(int type, int size) {
        this.size = size;
    }

    public static AsteroidSizes randomDirection(){
        AsteroidSizes[] asteroids = values();
        return asteroids[getRandom.nextInt(asteroids.length)];
    }

    public int getSize(){
        return this.size;
    }
}
