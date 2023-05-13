package gsbs.common.components;

public class Hitbox extends Component{

    private double width, height;
    private double x, y;
    private double hitboxPadding;

    public Hitbox(float width, float height, float x, float y) {
        hitboxPadding = Math.min(width, height) * 0.35f;
        this.width = (width + hitboxPadding) / 2.0f;
        this.height = (height + hitboxPadding) / 2.0f;
        this.x = x + width / 2f;
        this.y = y + height / 2f;
    }

    public void set(float x, float y){
        double spriteWidth = this.width * 2.0f;
        double spriteHeight = this.height * 2.0f;
        double offsetX = (spriteWidth - (width + hitboxPadding)) / 2.0f;
        double offsetY = (spriteHeight - (height + hitboxPadding)) / 2.0f;
        this.x = x + offsetX;
        this.y = y + offsetY;
    }

    public boolean intersects(Hitbox h){
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

    public double getWidth(){
        return width;
    }

    public double getHeight(){
        return height;
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }
}
