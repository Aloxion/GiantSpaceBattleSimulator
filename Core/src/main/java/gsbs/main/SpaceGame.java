package gsbs.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import gsbs.common.components.Graphics;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.entities.Entity;
import gsbs.common.services.IProcess;
import gsbs.common.services.IPlugin;
import gsbs.common.services.IPostProcess;
import gsbs.common.util.SPILocator;
import gsbs.managers.GameInputProcessor;

import java.util.Collection;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * Responsible for creating, rendering, updating and drawing.
 */
public class SpaceGame extends com.badlogic.gdx.Game{
    private final GameData gameData = new GameData();
    private final World world = new World();
    private ShapeRenderer sr;

    @Override
    public void create() {
        // Capture window size
        gameData.setDisplayWidth(Gdx.graphics.getWidth());
        gameData.setDisplayHeight(Gdx.graphics.getHeight());

        // Create a vector renderer
        sr = new ShapeRenderer();

        // Setup input handling
        Gdx.input.setInputProcessor(
                new GameInputProcessor(gameData)
        );

        // Load all Game Plugins using ServiceLoader
        for (IPlugin IPlugin : getPluginServices()) {
            IPlugin.start(gameData, world);
        }

    }


    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameData.setDeltaTime(Gdx.graphics.getDeltaTime());

        update();

        draw();

        gameData.getKeys().update();
    }


    private void update() {
        for (IProcess entityProcessorService : getEntityProcessingServices()) {
            entityProcessorService.process(gameData, world);
        }

        for (IPostProcess postEntityProcessorService : getPostEntityProcessingServices()) {
            postEntityProcessorService.process(gameData, world);
        }
    }

    private void draw() {
        for (Entity entity : world.getEntities()) {
            var graphics = entity.getComponent(Graphics.class);

            if (graphics == null) {
                continue;
            }

            sr.setColor(1, 1, 1, 1);

            sr.begin(ShapeRenderer.ShapeType.Line);

            for (int i = -1; i < graphics.shape.size() - 1; i++) {
                var current = graphics.shape.get(i == -1 ? graphics.shape.size() - 1 : i);

                var next = graphics.shape.get(i + 1);
                sr.line(current.x, current.y, next.x, next.y);
            }


            sr.end();
        }
    }

    private Collection<? extends IPlugin> getPluginServices() {
        ServiceLoader<IPlugin> serviceLoader = ServiceLoader.load(IPlugin.class);
        return serviceLoader.stream().map(ServiceLoader.Provider::get).collect(Collectors.toList());
    }

    private Collection<? extends IProcess> getEntityProcessingServices() {
        ServiceLoader<IProcess> serviceLoader = ServiceLoader.load(IProcess.class);
        return serviceLoader.stream().map(ServiceLoader.Provider::get).collect(Collectors.toList());
    }

    private Collection<? extends IPostProcess> getPostEntityProcessingServices() {
        ServiceLoader<IPostProcess> serviceLoader = ServiceLoader.load(IPostProcess.class);
        return serviceLoader.stream().map(ServiceLoader.Provider::get).collect(Collectors.toList());
    }
}