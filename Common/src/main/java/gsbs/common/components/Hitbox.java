package gsbs.common.components;

public class Hitbox extends Component {

    private final float width;
    private final float height;
    private float x, y;
    private final float hitboxPadding;

    public Hitbox(float width, float height, float x, float y) {
        hitboxPadding = Math.min(width, height) * 0.35f;
        this.width = (width + hitboxPadding) / 2.0f;
        this.height = (height + hitboxPadding) / 2.0f;
        this.x = x + width / 2f;
        this.y = y + height / 2f;
    }

    public void set(float x, float y) {
        float spriteWidth = this.width * 2.0f;
        float spriteHeight = this.height * 2.0f;
        float offsetX = (spriteWidth - (width + hitboxPadding)) / 2.0f;
        float offsetY = (spriteHeight - (height + hitboxPadding)) / 2.0f;
        this.x = x + offsetX;
        this.y = y + offsetY;
    }

    public boolean intersects(Hitbox h) {
        // TODO: THIS IS BROKEN, NOT TAKING ROTATED HITBOXES INTO ACCOUNT
        //Checks if entity1 right side intersects with entity2 left side
        boolean right = x + width >= h.x;
        //Checks if entity1 left side intersects with entity2 right side
        boolean left = h.x + h.width >= x;
        //Checks if entity1 top side intersects with entity2 bot side
        boolean top = y + height >= h.y;
        //Checks if entity1 bot side intersects with entity2 top side
        boolean bottom = h.y + h.height >= y;

        return right && left && bottom && top;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
