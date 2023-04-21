package gsbs.common.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Scaling;

import java.util.Random;

public class MySprite extends Component {
    private Texture texture;
    private Sprite sprite;

    private float width;
    private float height;


    public MySprite(){
    }

    /**
     * @param image = fx: "asteroid/default-asteroid.png"
     * @param width = Set in the plugin of entities, where you take account for the png size.
     * @param height = Set in the plugin of entities, where you take account for the png size.
     */
    public void setSprite(String image, int width, int height, Position position) {
        texture = new Texture(Gdx.files.internal(Gdx.files.getLocalStoragePath() + "/assets/sprites/" + image));
        sprite = new Sprite(texture, 0, 0, texture.getWidth(), texture.getHeight());
        sprite.setPosition(position.getX(), position.getY());
        sprite.setSize(width, height);
    }

    public Sprite getSprite(){
        return sprite;
    }

    public float getWidth(){
        return width;
    }

    public float getHeight(){
        return height;
    }

}
