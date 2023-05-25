package gsbs.common.components;

public class Hitbox extends Component {

    private final float halfWidth;
    private final float halfHeight;
    private float x, y;
    private final float hitboxPadding;

    public Hitbox(float width, float height, float x, float y) {
        hitboxPadding = Math.min(width, height) * 0.35f;
        this.halfWidth = (width + hitboxPadding) / 2.0f;
        this.halfHeight = (height + hitboxPadding) / 2.0f;
        this.x = x;
        this.y = y;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }


    public boolean intersects(Hitbox h) {

        //Checks if entity1 right side intersects with entity2 left side
        boolean right = x + halfWidth >= h.x;
        //Checks if entity1 left side intersects with entity2 right side
        boolean left = h.x + h.halfWidth >= x;
        //Checks if entity1 top side intersects with entity2 bot side
        boolean top = y + halfHeight >= h.y;
        //Checks if entity1 bot side intersects with entity2 top side
        boolean bottom = h.y + h.halfHeight >= y;

        return right && left && bottom && top;
    }

    public float getHalfWidth() {
        return halfWidth;
    }

    public float getHalfHeight() {
        return halfHeight;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
