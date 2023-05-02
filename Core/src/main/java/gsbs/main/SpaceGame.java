package gsbs.main;

import gsbs.common.components.Graphics;
import gsbs.common.components.Hitbox;
import gsbs.common.components.Position;
import gsbs.common.components.Sprite;
import gsbs.common.data.GameData;
import gsbs.common.data.GameKeys;
import gsbs.common.data.GameState;
import gsbs.common.data.World;
import gsbs.common.entities.Entity;
import gsbs.common.events.Event;
import gsbs.common.events.GameLoseEvent;
import gsbs.common.events.GameWinEvent;
import gsbs.common.services.*;
import gsbs.common.util.Plugin;
import gsbs.common.util.PluginManager;
import gsbs.util.Configuration;
import gsbs.util.PciIdParser;
import gsbs.util.Window;
import imgui.ImDrawList;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.ImGui;
import org.lwjgl.bgfx.BGFX;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGPaint;

import java.net.URL;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static gsbs.util.Color.rgba;
import static org.lwjgl.nanovg.NanoVG.*;

/**
 * Responsible for creating, rendering, updating and drawing.
 */
public class SpaceGame implements IEventListener {
    private final Window window;
    private final long nvgContext;
    PciIdParser pciParser = new PciIdParser("/pci.ids.txt");
    private GameData gameData = new GameData();
    private World world = new World();
    private boolean paused = false;
    private Entity selectedEntity = null;
    private boolean showHitbox = false;

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
        this.gameData.getEventManager().dispatchEvents(gameData, getEventListeners());

        renderGUI();

        update();

        draw();

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
                }
                break;
            case QUIT:
                GLFW.glfwSetWindowShouldClose(window.getHandle(), true);
                break;
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

        //Draw hitbox
        for (Entity entity : world.getEntitiesWithComponents(Position.class, Hitbox.class)) {
            var hitbox = entity.getComponent(Hitbox.class);
            var position = entity.getComponent(Position.class);

            if (showHitbox) {
                nvgSave(nvgContext);
                nvgTranslate(nvgContext, hitbox.getX() + hitbox.getWidth() / 2.0f, hitbox.getY() + hitbox.getHeight() / 2.0f);
                nvgRotate(nvgContext, position.getRadians());
                nvgTranslate(nvgContext, -hitbox.getWidth() / 2.0f, -hitbox.getHeight() / 2.0f);

                nvgBeginPath(nvgContext);
                nvgRect(nvgContext, 0, 0, hitbox.getWidth(), hitbox.getHeight());
                nvgFillColor(nvgContext, rgba(255, 0, 0, 1));
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

    private Collection<? extends IEventListener> getEventListeners() {
        var eventListeners = PluginManager.locateAll(IEventListener.class);
        eventListeners.add(this);
        return eventListeners;
    }

    private void renderGUI() {
        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(gameData.getDisplayWidth() * 0.3f, gameData.getDisplayHeight());
        ImGui.setNextWindowCollapsed(true, ImGuiCond.FirstUseEver);

        ImGui.begin("Debug Menu", ImGuiWindowFlags.NoResize);

        if (ImGui.collapsingHeader("Inspector")) {
            if (selectedEntity == null) {
                ImGui.text("No entity selected.");
            } else {
                ImGui.text("ID: " + selectedEntity.getID());

                if (ImGui.treeNode("Components")) {
                    for (var component : selectedEntity.getComponents()) {
                        if (ImGui.treeNode(component.getClass().getSimpleName() + "##" + component)) {
                            for (var field : component.getClass().getDeclaredFields()) {
                                field.setAccessible(true);
                                try {
                                    ImGui.text(field.getName() + ": " + field.get(component));
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            ImGui.treePop();
                        }
                    }
                    ImGui.treePop();
                }
            }
        }

        if (ImGui.collapsingHeader("World")) {
            if (ImGui.beginListBox("## entities", Float.MIN_VALUE, 5 * ImGui.getTextLineHeightWithSpacing())) {
                Map<Class<? extends Entity>, List<Entity>> entitiesGrouped =
                        world.getEntities().stream().collect(Collectors.groupingBy(Entity::getClass));

                for (var entityType : entitiesGrouped.keySet()) {
                    if (ImGui.treeNode(entityType.getSimpleName() + " ## " + entityType.getName())) {
                        for (var entity : entitiesGrouped.get(entityType)) {
                            boolean isSelected = selectedEntity != null && selectedEntity.equals(entity);

                            if (ImGui.selectable(entity.getID(), isSelected)) {
                                selectedEntity = entity;
                            }

                            if (isSelected) {
                                ImGui.setItemDefaultFocus();
                            }
                        }
                        ImGui.treePop();
                    }
                }
                ImGui.endListBox();
            }
        }

        if (ImGui.collapsingHeader("Plugins")) {
            if (ImGui.beginTable("plugins_table", 1, ImGuiTableFlags.Borders + ImGuiTableFlags.Resizable)) {
                for (Map.Entry<URL, Plugin> plugin : PluginManager.getPlugins().entrySet()) {
                    ImGui.tableNextRow();
                    ImGui.tableSetColumnIndex(0);
                    ImGui.text(Paths.get(plugin.getKey().getFile()).getFileName().toString());
                }
            }
            ImGui.endTable();

            if (ImGui.button("Update")) {
                var pluginsToBeUnloaded = PluginManager.updatePluginLayers();

                for (var pluginURL : pluginsToBeUnloaded) {
                    PluginManager.getPlugins().get(pluginURL).getServices(IPlugin.class).forEach(s -> {
                        s.stop(gameData, world);
                    });
                }

                PluginManager.reloadPlugins();
            }
        }

        if (ImGui.collapsingHeader("Stats")) {
            ImGui.text("FPS: " + (int) (1 / gameData.getDeltaTime()));
            String vendorId = Integer.toHexString(BGFX.bgfx_get_caps().vendorId() & 0xffff);
            String deviceId = Integer.toHexString(BGFX.bgfx_get_caps().deviceId() & 0xffff);
            ImGui.text("GPU: " + pciParser.lookupName(vendorId, deviceId));
            ImGui.text("Graphics API: " + BGFX.bgfx_get_renderer_name(BGFX.bgfx_get_renderer_type()));
        }

        ImDrawList drawList = ImGui.getWindowDrawList();

        drawList.addRectFilled(ImGui.getCursorScreenPosX() + 4, ImGui.getCursorScreenPosY() + 4, ImGui.getCursorScreenPosX() + 8, ImGui.getCursorScreenPosY() + 16, ImGui.getColorU32(paused ? ImGuiCol.TextDisabled : ImGuiCol.Text));
        drawList.addRectFilled(ImGui.getCursorScreenPosX() + 12, ImGui.getCursorScreenPosY() + 4, ImGui.getCursorScreenPosX() + 16, ImGui.getCursorScreenPosY() + 16, ImGui.getColorU32(paused ? ImGuiCol.TextDisabled : ImGuiCol.Text));

        ImGui.beginDisabled(paused);
        if (ImGui.button("## pause", 20, 20)) {
            paused = true;
        }
        ImGui.endDisabled();

        ImGui.sameLine();

        drawList.addTriangleFilled(ImGui.getCursorScreenPosX() + 4, ImGui.getCursorScreenPosY() + 4, ImGui.getCursorScreenPosX() + 16, ImGui.getCursorScreenPosY() + 10, ImGui.getCursorScreenPosX() + 4, ImGui.getCursorScreenPosY() + 16, ImGui.getColorU32(paused ? ImGuiCol.Text : ImGuiCol.TextDisabled));

        ImGui.beginDisabled(!paused);
        if (ImGui.button("## play", 20, 20)) {
            paused = false;
        }
        ImGui.endDisabled();


        ImGui.sameLine();


        drawList.addRectFilled(ImGui.getCursorScreenPosX() + 4, ImGui.getCursorScreenPosY() + 4, ImGui.getCursorScreenPosX() + 16, ImGui.getCursorScreenPosY() + 16, ImGui.getColorU32(ImGuiCol.Text));

        if (ImGui.button("## stop", 20, 20)) {
            GLFW.glfwSetWindowShouldClose(window.getHandle(), true);
        }

        ImDrawList drawList2 = ImGui.getWindowDrawList();

        drawList2.addText(ImGui.getCursorScreenPosX() + 6, ImGui.getCursorScreenPosY() + 4, ImGui.getColorU32(ImGuiCol.Text), "Hitbox");
        if (ImGui.button("## hitbox", 60, 20)) {
            showHitbox = !showHitbox;
        }

        if (ImGui.button("Restart")) {
            this.gameData.setGameState(GameState.START);
        }

        if (ImGui.button("Win")) {
            this.gameData.getEventManager().addEvent(new GameWinEvent(null));
        }

        if (ImGui.button("Lose")) {
            this.gameData.getEventManager().addEvent(new GameLoseEvent(null));
        }

        ImGui.end();
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
