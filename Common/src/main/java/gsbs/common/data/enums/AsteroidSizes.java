package gsbs.common.data.enums;

import java.util.Random;

public enum AsteroidSizes {
    Small(1, 256/4),
    Medium(2, 256/2),
    Large(3, 256);

    private static final Random getRandom = new Random();

    AsteroidSizes(int type, int size) {

    }

    public static AsteroidSizes randomDirection(){
        AsteroidSizes[] asteroids = values();
        return asteroids[getRandom.nextInt(asteroids.length)];
    }
}
