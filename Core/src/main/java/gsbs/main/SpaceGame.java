package gsbs.main;

import gsbs.common.components.Graphics;
import gsbs.common.components.Position;
import gsbs.common.components.Sprite;
import gsbs.common.data.GameData;
import gsbs.common.data.GameKeys;
import gsbs.common.data.Grid;
import gsbs.common.data.World;
import gsbs.common.entities.Entity;
import gsbs.common.events.EventManager;
import gsbs.common.services.IEventListener;
import gsbs.common.services.IPlugin;
import gsbs.common.services.IPostProcess;
import gsbs.common.services.IProcess;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static gsbs.util.Color.rgba;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.nanovg.NanoVG.*;

/**
 * Responsible for creating, rendering, updating and drawing.
 */
public class SpaceGame {
    private final Window window;
    private final long nvgContext;
    private final GameData gameData = new GameData();
    private final World world = new World();

    // Track which plugins have been initialized
    private final List<IPlugin> initializedPlugins = new ArrayList<>();
    private final EventManager eventManager;
    PciIdParser pciParser = new PciIdParser("/pci.ids.txt");
    private boolean paused = false;
    private Entity selectedEntity = null;

    public SpaceGame(Configuration config) {
        this.eventManager = new EventManager();
        this.window = new Window(config, this::run);
        this.nvgContext = this.window.getNvgContext();
    }

    public void start() {
        // Capture window size
        int[] width = new int[1];
        int[] height = new int[1];

        glfwGetWindowSize(window.getHandle(), width, height);

        gameData.setDisplayWidth(width[0]);
        gameData.setDisplayHeight(height[0]);
        gameData.setGrid(new Grid(50, width[0], height[0]));

        System.out.println(getEventListeners());
        for (IEventListener eventListener : getEventListeners()) {
            eventManager.addEventListener(eventListener);
        }

        this.window.run();
    }

    private void run(Window window) {
        gameData.setDeltaTime(ImGui.getIO().getDeltaTime());
        gameData.setRenderCycles(gameData.getRenderCycles()+1);
        gameData.getGrid().updateGrid(world);

        renderGUI();

        update();

        draw();

        gameData.getKeys().update();
    }

    private void update() {
        if (paused)
            return;

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

        for (IPlugin iGamePlugin : getPluginServices()) {
            if (!initializedPlugins.contains(iGamePlugin)) {
                iGamePlugin.start(gameData, world);
                initializedPlugins.add(iGamePlugin);
            }
        }

        for (IProcess entityProcessorService : getProcessingServices()) {
            entityProcessorService.process(gameData, world);
        }

        for (IPostProcess postEntityProcessorService : getPostProcessingServices()) {
            postEntityProcessorService.process(gameData, world);
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
                nvgImagePattern(nvgContext, 0, 0, sprite.getWidth(), sprite.getHeight(), (float) (Math.PI/2.0f), sprite.getSpriteId(nvgContext), 1, img);

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

    private Collection<? extends IEventListener> getEventListeners() {
        return PluginManager.locateAll(IEventListener.class);
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

        ImGui.newLine();


        drawList.addRectFilled(ImGui.getCursorScreenPosX() + 4, ImGui.getCursorScreenPosY() + 4, ImGui.getCursorScreenPosX() + 16, ImGui.getCursorScreenPosY() + 16, ImGui.getColorU32(ImGuiCol.Text));

        if (ImGui.button("## grid", 20, 20)) {

        }

        ImGui.end();
    }
}
