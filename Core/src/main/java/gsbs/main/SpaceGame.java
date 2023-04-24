package gsbs.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import gsbs.common.components.Graphics;
import gsbs.common.components.MySprite;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.entities.Entity;
import gsbs.common.events.EventManager;
import gsbs.common.events.IEventListener;
import gsbs.common.services.IProcess;
import gsbs.common.services.IPlugin;
import gsbs.common.services.IPostProcess;
import gsbs.managers.GameInputProcessor;

import java.util.Collection;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * Responsible for creating, rendering, updating and drawing.
 */
public class SpaceGame extends ApplicationAdapter {

    private static OrthographicCamera cam;
    private final GameData gameData = new GameData();
    private final World world = new World();
    private ShapeRenderer sr;
    private EventManager eventManager;
    private SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        // Capture window size
        if (gameData.getDisplayWidth() != Gdx.graphics.getWidth() || gameData.getDisplayHeight() != Gdx.graphics.getHeight()
        ) {
            this.updateCam(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        // Create a vector renderer
        sr = new ShapeRenderer();
        eventManager = new EventManager();

        // Setup input handling
        Gdx.input.setInputProcessor(
                new GameInputProcessor(gameData, eventManager)
        );

        // Load all Game Plugins using ServiceLoader
        for (IPlugin IPlugin : getPluginServices()) {
            IPlugin.start(gameData, world);
        }

        System.out.println(getEventListeners());
        for (IEventListener eventListener : getEventListeners()) {
            eventManager.addEventListener(eventListener);
        }
    }

    private void updateCam(int width, int height) {
        gameData.setDisplayWidth(width);
        gameData.setDisplayHeight(height);

        cam = new OrthographicCamera(gameData.getDisplayWidth(), gameData.getDisplayHeight());
        cam.setToOrtho(false, gameData.getDisplayWidth(), gameData.getDisplayHeight());
        cam.position.set((float) gameData.getDisplayWidth(), (float) gameData.getDisplayHeight(),0);
        cam.update();
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
        for (IProcess entityProcessorService : getProcessingServices()) {
            entityProcessorService.process(gameData, world);
        }

        eventManager.dispatchEvents(gameData);

        for (IPostProcess postEntityProcessorService : getPostProcessingServices()) {
            postEntityProcessorService.process(gameData, world);
        }
    }

    private void draw() {
        for (Entity entity : world.getEntities()) {
            Graphics graphics = entity.getComponent(Graphics.class);
            MySprite mySprite = entity.getComponent(MySprite.class);

            if (graphics != null){
                sr.setColor(1, 1, 1, 1);

                sr.begin(ShapeRenderer.ShapeType.Line);

                for (int i = -1; i < graphics.shape.size() - 1; i++) {
                    var current = graphics.shape.get(i == -1 ? graphics.shape.size() - 1 : i);

                    var next = graphics.shape.get(i + 1);
                    sr.line(current.x, current.y, next.x, next.y);
                }
                sr.end();
            }
            if (mySprite != null){
                batch.begin();
                        mySprite.getSprite().draw(batch);
                batch.end();
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    private Collection<? extends IPlugin> getPluginServices() {
        ServiceLoader<IPlugin> serviceLoader = ServiceLoader.load(IPlugin.class);
        return serviceLoader.stream().map(ServiceLoader.Provider::get).collect(Collectors.toList());
    }

    private Collection<? extends IProcess> getProcessingServices() {
        ServiceLoader<IProcess> serviceLoader = ServiceLoader.load(IProcess.class);
        return serviceLoader.stream().map(ServiceLoader.Provider::get).collect(Collectors.toList());
    }

    private Collection<? extends IPostProcess> getPostProcessingServices() {
        ServiceLoader<IPostProcess> serviceLoader = ServiceLoader.load(IPostProcess.class);
        return serviceLoader.stream().map(ServiceLoader.Provider::get).collect(Collectors.toList());
    }

    private Collection<? extends IEventListener> getEventListeners() {
        ServiceLoader<IEventListener> serviceLoader = ServiceLoader.load(IEventListener.class);
        return serviceLoader.stream().map(ServiceLoader.Provider::get).collect(Collectors.toList());
    }
}
