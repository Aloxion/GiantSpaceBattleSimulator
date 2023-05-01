package gsbs.common.components;

public class Hitbox extends Component{

    private float width, height;
    private float x, y;

    public Hitbox (float width, float height, float x, float y){
        this.height = height;
        this.width = width;
        this.x = x;
        this.y = y;
    }

    public void Set(float x, float y){
        this.x = x;
        this.y = y;
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
}
