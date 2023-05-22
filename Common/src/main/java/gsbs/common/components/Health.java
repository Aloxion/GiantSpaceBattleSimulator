package gsbs.common.components;

public class Health extends Component {
    private final int initialHealth;
    private int healthPoints;

    public Health(int healthPoints) {
        this.initialHealth = healthPoints;
        this.healthPoints = healthPoints;
    }

    public int getInitialHealth() {
        return initialHealth;
    }

    public int getHealthPoints() {
        return healthPoints;
    }

    public void removeHealthPoints(int amount) {
        healthPoints = Math.max(0, healthPoints - amount);
    }

    public boolean isDead() {
        return healthPoints <= 0;
    }
}
