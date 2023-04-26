package gsbs.main;

import gsbs.util.Color;
import gsbs.util.Configuration;

public class Main {
    public static void main(String[] args) {
        var cfg = new Configuration();
        cfg.setTitle("Giant Space Battle Simulator");
        cfg.setWidth(800);
        cfg.setHeight(600);
        cfg.setFullScreen(false);
        cfg.setVsync(true);
        cfg.setBackgroundColor(new Color(0, 0, 0, 1));

        new SpaceGame(cfg).start();
    }
}
