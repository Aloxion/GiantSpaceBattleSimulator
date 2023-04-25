package gsbs.main;

import gsbs.util.Configuration;

public class Main {
    public static void main(String[] args) {
        var cfg = new Configuration();
        cfg.setTitle("Giant Space Battle Simulator");
        cfg.setWidth(500);
        cfg.setHeight(400);
        cfg.setFullScreen(false);
        cfg.setVsync(true);

        new SpaceGame(cfg).start();
    }
}
