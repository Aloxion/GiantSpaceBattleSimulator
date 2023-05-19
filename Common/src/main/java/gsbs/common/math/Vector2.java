package gsbs.common.math;

public class Vector2 {
    public float x;
    public float y;

    public Vector2() {
        this.x = 0.0f;
        this.y = 0.0f;
    }

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public boolean equals(Vector2 other) {
        return (this.x == other.x && this.y == other.y);
    }


    public double dot(Vector2 other) {
        return this.x * other.x + this.y * other.y;
    }

    public double magnitude() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }


    public Vector2 add(Vector2 other) {
        return new Vector2(this.x + other.x, this.y + other.y);
    }

    public Vector2 subtract(Vector2 other) {
        return new Vector2(this.x - other.x, this.y - other.y);
    }

    public Vector2 divide(float other) {
        return new Vector2(this.x / other, this.y / other);
    }

    public Vector2 multiply(float other) {
        return new Vector2(this.x * other, this.y * other);
    }

    public Vector2 withLength(float length) {
        return this.normalize().multiply(length);
    }

    public Vector2 normalize() {
        float length = this.length();
        // here we multiply by the reciprocal instead of calling 'div()'
        // since div duplicates this zero check.
        if (length == 0)
            return new Vector2();
        else
            return this.divide(length);
    }

    public Vector2 limit(float limit) {
        Vector2 newVector = new Vector2(this.x, this.y);
        if (newVector.length() > limit) {
            newVector.x = newVector.x / newVector.length() * limit;
            newVector.y = newVector.y / newVector.length() * limit;
        }
        return newVector;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public float direction() {
        return (float) Math.atan2(this.y, this.x);
    }
}