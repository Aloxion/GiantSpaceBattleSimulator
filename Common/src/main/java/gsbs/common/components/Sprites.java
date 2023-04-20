package gsbs.common.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import gsbs.common.entities.Entity;

public class Sprites extends Component {
    private Texture texture;

    private float width;
    private float height;


    public Sprites(){
    }

    public void setTexture(String image, float width, float height){
        texture = new Texture(Gdx.files.internal(Gdx.files.getLocalStoragePath()+"/assets/sprites/"+image));
        this.width = width;
        this.height = height;
    }

    public Texture getTexture(){
        return texture;
    }

    public float getWidth(){
        return width;
    }

    public float getHeight(){
        return height;
    }

}
