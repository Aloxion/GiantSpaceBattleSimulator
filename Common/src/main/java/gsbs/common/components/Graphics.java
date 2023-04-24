package gsbs.common.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import gsbs.common.entities.Entity;
import gsbs.common.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Graphics extends Component {

    public Graphics(){

    }
    public List<Vector2> shape = new ArrayList<>();

    public List<Vector2> getShape() {
        return this.shape;
    }

    public void setShape(List<Vector2> shape) {
        this.shape = shape;
    }
}
