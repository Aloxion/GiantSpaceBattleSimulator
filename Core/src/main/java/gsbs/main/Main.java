package gsbs.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class Main {
    public static void main(String[] args) {
        var cfg = new LwjglApplicationConfiguration();
        cfg.title = "Giant Space Battle Simulator";
        cfg.width = 1200;
        cfg.height = 800;
        cfg.resizable = false;
        cfg.vSyncEnabled = true;
        cfg.forceExit = false;

        new LwjglApplication(new SpaceGame(), cfg);
    }
}
