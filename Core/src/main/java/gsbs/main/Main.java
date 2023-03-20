package gsbs.main;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
    public static void main(String[] args) {
        var cfg = new LwjglApplicationConfiguration();
        cfg.title = "Giant Space Battle Simulator";
        cfg.width = 500;
        cfg.height = 400;
        cfg.resizable = false;
        cfg.vSyncEnabled = true;
        cfg.forceExit = false;

        new LwjglApplication(new SpaceGame(), cfg);
    }
}
