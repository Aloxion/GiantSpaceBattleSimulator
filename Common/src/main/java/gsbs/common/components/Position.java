package gsbs.common.components;

import gsbs.common.math.Vector2;

public class Position extends Component {
    private float x;
    private float y;
    private float radians;

    public Position(float x, float y, float radians) {
        this.x = x;
        this.y = y;
        this.radians = radians;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getRadians() {
        return radians;
    }

    public void setRadians(float radians) {
        this.radians = radians;
    }

    public Vector2 asVector() {
        return new Vector2(getX(), getY());
    }
}
