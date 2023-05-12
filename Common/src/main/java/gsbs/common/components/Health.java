package gsbs.common.components;

public class Health extends Component {
    private int healthPoints;

    public Health(int healthPoints) {
        this.healthPoints = healthPoints;
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
