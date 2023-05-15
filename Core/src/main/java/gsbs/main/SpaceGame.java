package gsbs.main;

import gsbs.common.components.Graphics;
import gsbs.common.components.Position;
import gsbs.common.components.Sprite;
import gsbs.common.data.GameData;
import gsbs.common.data.GameKeys;
import gsbs.common.data.Grid;
import gsbs.common.data.World;
import gsbs.common.entities.Entity;
import gsbs.common.events.Event;
import gsbs.common.events.GameLoseEvent;
import gsbs.common.events.GameWinEvent;
import gsbs.common.services.*;
import gsbs.common.util.PluginManager;
import gsbs.util.Configuration;
import gsbs.util.Window;
import imgui.internal.ImGui;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGPaint;

import java.util.Collection;

import static gsbs.common.util.Color.rgba;
import static org.lwjgl.nanovg.NanoVG.*;

/**
 * Responsible for creating, rendering, updating and drawing.
 */
public class SpaceGame implements IEventListener {
    private final Window window;
    private final long nvgContext;
    private GameData gameData = new GameData();
    private World world = new World();

    public SpaceGame(Configuration config) {
        this.window = new Window(config, this::run);
        this.nvgContext = this.window.getNvgContext();
    }

    public void start() {
        this.window.run();
    }

    private void run(Window window) {
        gameData.setDeltaTime(ImGui.getIO().getDeltaTime());
        gameData.setRenderCycles(gameData.getRenderCycles() + 1);
        gameData.setNvgContext(nvgContext);
        this.gameData.getEventManager().dispatchEvents(gameData, getEventListeners());

        if (gameData.getGrid() == null) {
            gameData.setGrid(new Grid(gameData.getNodeSize(), gameData.getDisplayWidth(), gameData.getDisplayHeight()));
        }

        update();

        gameData.getKeys().update();
    }

    private void update() {
        // Handle input
        try {
            for (var key : GameKeys.Keys.class.getDeclaredFields()) {
                if (ImGui.isKeyPressed(key.getInt(key))) {
                    gameData.getKeys().setKey(key.getInt(key), true);
                } else if (ImGui.isKeyReleased(key.getInt(key))) {
                    gameData.getKeys().setKey(key.getInt(key), false);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        for (ISystemProcess systemProcess : getSystemProcessingServices()) {
            systemProcess.process(gameData, world);
        }

        switch (this.gameData.getGameState()) {
            case IN_GAME:
                for (IPlugin iGamePlugin : getPluginServices()) {
                    if (!gameData.getInitializedPlugins().contains(iGamePlugin)) {
                        iGamePlugin.start(gameData, world);
                        gameData.getInitializedPlugins().add(iGamePlugin);
                    }
                }

                for (IProcess entityProcessorService : getProcessingServices()) {
                    entityProcessorService.process(gameData, world);
                }

                for (IPostProcess postEntityProcessorService : getPostProcessingServices()) {
                    postEntityProcessorService.process(gameData, world);
                    gameData.getGrid().updateGrid(world);
                }

                break;
            case QUIT:
                GLFW.glfwSetWindowShouldClose(window.getHandle(), true);
                break;
        }

        draw();

        for (ISystemPostProcess systemProcess : getSystemPostProcessingServices()) {
            systemProcess.process(gameData, world);
        }
    }

    private void draw() {
        // Draw vector graphics
        for (Entity entity : world.getEntitiesWithComponent(Graphics.class)) {
            var graphics = entity.getComponent(Graphics.class);

            if (graphics == null || graphics.shape.size() == 0) {
                continue;
            }

            nvgBeginPath(nvgContext);
            nvgMoveTo(nvgContext, graphics.shape.get(0).x, graphics.shape.get(0).y);
            for (int i = 1; i < graphics.shape.size(); i++) {
                nvgLineTo(nvgContext, graphics.shape.get(i).x, graphics.shape.get(i).y);
            }
            nvgLineTo(nvgContext, graphics.shape.get(0).x, graphics.shape.get(0).y);
            nvgStrokeColor(nvgContext, rgba(1, 1, 1, 1));
            nvgStroke(nvgContext);
        }

        // Draw sprites
        for (Entity entity : world.getEntitiesWithComponents(Position.class, Sprite.class)) {
            var position = entity.getComponent(Position.class);
            var sprite = entity.getComponent(Sprite.class);

            float cx = sprite.getWidth() / 2.0f;
            float cy = sprite.getHeight() / 2.0f;

            nvgSave(nvgContext);
            nvgTranslate(nvgContext, position.getX() + cx, position.getY() + cy);
            nvgRotate(nvgContext, position.getRadians());
            nvgTranslate(nvgContext, -cx, -cy);

            try (NVGPaint img = NVGPaint.calloc()) {
                nvgImagePattern(nvgContext, 0, 0, sprite.getWidth(), sprite.getHeight(), (float) (Math.PI / 2.0f), sprite.getSpriteId(nvgContext), 1, img);
                nvgBeginPath(nvgContext);
                nvgRect(nvgContext, 0, 0, sprite.getWidth(), sprite.getHeight());
                nvgFillPaint(nvgContext, img);
                nvgFill(nvgContext);
                nvgRestore(nvgContext);
            }
        }
    }

    private Collection<? extends IPlugin> getPluginServices() {
        return PluginManager.locateAll(IPlugin.class);
    }

    private Collection<? extends IProcess> getProcessingServices() {
        return PluginManager.locateAll(IProcess.class);
    }

    private Collection<? extends IPostProcess> getPostProcessingServices() {
        return PluginManager.locateAll(IPostProcess.class);
    }

    private Collection<? extends ISystemProcess> getSystemProcessingServices() {
        return PluginManager.locateAll(ISystemProcess.class);
    }

    private Collection<? extends ISystemPostProcess> getSystemPostProcessingServices() {
        return PluginManager.locateAll(ISystemPostProcess.class);
    }

    private Collection<? extends IEventListener> getEventListeners() {
        var eventListeners = PluginManager.locateAll(IEventListener.class);
        eventListeners.add(this);
        return eventListeners;
    }

    @Override
    public void onEvent(Event event, GameData gameData) {
        // Handle events
        if (event instanceof GameWinEvent) {
            this.world = new World();
            this.gameData = new GameData();
        } else if (event instanceof GameLoseEvent) {
            this.world = new World();
            this.gameData = new GameData();
        }
    }
}
