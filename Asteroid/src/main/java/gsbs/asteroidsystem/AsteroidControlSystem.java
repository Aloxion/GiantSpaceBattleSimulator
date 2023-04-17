package gsbs.asteroidsystem;

import gsbs.common.components.Graphics;
import gsbs.common.components.Movement;
import gsbs.common.components.Position;
import gsbs.common.data.GameData;
import gsbs.common.data.GameKeys;
import gsbs.common.data.World;
import gsbs.common.entities.Entity;
import gsbs.common.entities.Asteroid;
import gsbs.common.math.Vector2;
import gsbs.common.services.IProcess;

import java.util.ArrayList;
import java.util.List;

public class AsteroidControlSystem implements IProcess {
    private AsteroidPlugin asteroidPlugin = new AsteroidPlugin();
    @Override
    public void process(GameData gameData, World world) {
        if (world.getEntities(Asteroid.class).size() < 8) {
            asteroidPlugin.start(gameData, world);
        }
    }
}