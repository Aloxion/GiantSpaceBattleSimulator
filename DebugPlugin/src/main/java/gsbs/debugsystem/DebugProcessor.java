package gsbs.debugsystem;

import gsbs.common.components.Hitbox;
import gsbs.common.components.Position;
import gsbs.common.data.GameData;
import gsbs.common.data.GameState;
import gsbs.common.data.Node;
import gsbs.common.data.World;
import gsbs.common.entities.Entity;
import gsbs.common.events.GameLoseEvent;
import gsbs.common.events.GameWinEvent;
import gsbs.common.services.IPlugin;
import gsbs.common.services.ISystemPostProcess;
import gsbs.common.util.Color;
import gsbs.common.util.Plugin;
import gsbs.common.util.PluginManager;
import imgui.ImDrawList;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.ImGui;
import org.lwjgl.bgfx.BGFX;

import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static gsbs.common.util.Color.rgba;
import static java.awt.Color.getHSBColor;
import static org.lwjgl.nanovg.NanoVG.*;

public class DebugProcessor implements ISystemPostProcess {
    PciIdParser pciParser = new PciIdParser("/pci.ids.txt");
    private Entity selectedEntity = null;
    private boolean showHitbox = false;
    private boolean showGrid = false;
    private boolean showCenter = false;
    private boolean paused = false;

    @Override
    public void process(GameData gameData, World world) {
        long nvgContext = gameData.getNvgContext();

        if (showHitbox) {
            for (Entity entity : world.getEntitiesWithComponents(Position.class, Hitbox.class)) {
                var hitbox = entity.getComponent(Hitbox.class);
                var position = entity.getComponent(Position.class);

                nvgSave(nvgContext);
                nvgTranslate(nvgContext, (hitbox.getX()), (hitbox.getY()));
                nvgRotate(nvgContext, position.getRadians());
                nvgTranslate(nvgContext, (-hitbox.getHalfWidth()/2), (-hitbox.getHalfHeight())/2);

                nvgBeginPath(nvgContext);
                nvgRect(nvgContext, 0, 0, hitbox.getHalfWidth(), hitbox.getHalfHeight());
                nvgFillColor(nvgContext, rgba(255, 0, 0, 1));
                nvgFill(nvgContext);
                nvgRestore(nvgContext);

            }
        }
        //Draw Center
        if(showCenter){
            for (Entity entity : world.getEntitiesWithComponents(Position.class)) {
                var position = entity.getComponent(Position.class);
                nvgBeginPath(nvgContext);
                nvgCircle(nvgContext, position.getX(), position.getY(), 1);
                nvgStroke(nvgContext);
            }
        }
        if (showGrid) {
            for (int i = 0; i < gameData.getDisplayWidth() / gameData.getNodeSize(); i++) {
                for (int j = 0; j < gameData.getDisplayHeight() / gameData.getNodeSize(); j++) {
                    Node node = gameData.getGrid().getNode(i, j);
                    int nodeSize = gameData.getGrid().getNodeSize();
                    float nodeX = gameData.getGrid().getCoordsFromNode(node)[0] - nodeSize/2;
                    float nodeY = gameData.getGrid().getCoordsFromNode(node)[1] - nodeSize/2;

                    // Draw the filled rectangle with transparency
                    nvgBeginPath(nvgContext);
                    nvgRect(nvgContext, nodeX, nodeY, nodeSize, nodeSize);
                    nvgFillColor(nvgContext, rgba(255, 255, 255, 0));
                    nvgFill(nvgContext);


                    if (!node.isBlocked()) {
                        // Draw the stroke (edge) of the rectangle
                        nvgStrokeColor(nvgContext, rgba(255, 255, 255, 0.1f));
                    } else {
                        // Draw the stroke (edge) of the rectangle
                        nvgStrokeColor(nvgContext, rgba(255, 0, 0, 0.1f));
                    }
                    nvgStrokeWidth(nvgContext, 0.085f);
                    nvgStroke(nvgContext);

                }
            }

            for (var path : gameData.getPaths().entrySet()) {
                if (world.getEntity(path.getKey().getID()) == null) {
                    continue;
                }

                List<Node> nodes = path.getValue();
                Random random = new Random(path.getKey().getID().hashCode());
                final float hue = random.nextFloat();
                final float saturation = 0.9f;//1.0 for brilliant, 0.0 for dull
                final float luminance = 1.0f; //1.0 for brighter, 0.0 for black
                var color = getHSBColor(hue, saturation, luminance);
                var pathColor = new Color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 255);

                if (nodes != null && nodes.size() > 1) {
                    nvgBeginPath(nvgContext);
                    Node firstNode = nodes.get(0);
                    int[] firstCoords = gameData.getGrid().getCoordsFromNode(firstNode);
                    nvgMoveTo(nvgContext, firstCoords[0], firstCoords[1]);

                    for (int i = 1; i < nodes.size(); i++) {
                        Node node = nodes.get(i);
                        int[] coords = gameData.getGrid().getCoordsFromNode(node);
                        nvgLineTo(nvgContext, coords[0], coords[1]);
                    }

                    nvgStrokeColor(nvgContext, rgba(pathColor.getRed(), pathColor.getGreen(), pathColor.getBlue(), 1f));
                    nvgStrokeWidth(nvgContext, 3f);
                    nvgStroke(nvgContext);
                }

                // Draw circles for each node
                if (nodes != null) {
                    for (Node node : nodes) {
                        nvgBeginPath(nvgContext);
                        int[] coords = gameData.getGrid().getCoordsFromNode(node);
                        nvgCircle(nvgContext, coords[0], coords[1], 10);
                        nvgFillColor(nvgContext, rgba(255, 255, 255, 0));
                        nvgFill(nvgContext);
                        nvgStrokeColor(nvgContext, rgba(pathColor.getRed(), pathColor.getGreen(), pathColor.getBlue(), 1f));
                        nvgStrokeWidth(nvgContext, 1.0f);
                        nvgStroke(nvgContext);
                    }
                }

            }
        }
        renderGUI(gameData, world);
    }


    private void renderGUI(GameData gameData, World world) {
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
            ImGui.text("Entities: " + world.getEntities().size());
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

        if (ImGui.collapsingHeader("Profiling")) {
            if (ImGui.beginListBox("## profiling", Float.MIN_VALUE, 15 * ImGui.getTextLineHeightWithSpacing())) {
                for (var profilingData : gameData.getProfilingData().entrySet()) {
                    ImGui.text(profilingData.getKey().getSimpleName() + ": " + profilingData.getValue() / 1000000.f + "ms");
                }
                ImGui.endListBox();
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
            gameData.setGameState(GameState.PAUSED);
            paused = true;
        }
        ImGui.endDisabled();

        ImGui.sameLine();

        drawList.addTriangleFilled(ImGui.getCursorScreenPosX() + 4, ImGui.getCursorScreenPosY() + 4, ImGui.getCursorScreenPosX() + 16, ImGui.getCursorScreenPosY() + 10, ImGui.getCursorScreenPosX() + 4, ImGui.getCursorScreenPosY() + 16, ImGui.getColorU32(paused ? ImGuiCol.Text : ImGuiCol.TextDisabled));

        ImGui.beginDisabled(!paused);
        if (ImGui.button("## play", 20, 20)) {
            paused = false;
            gameData.setGameState(GameState.IN_GAME);
        }
        ImGui.endDisabled();

        ImDrawList drawList2 = ImGui.getWindowDrawList();

        drawList2.addText(ImGui.getCursorScreenPosX() + 6, ImGui.getCursorScreenPosY() + 4, ImGui.getColorU32(ImGuiCol.Text), "Hitbox");
        if (ImGui.button("## hitbox", 50, 20)) {
            showHitbox = !showHitbox;
        }
        drawList2.addText(ImGui.getCursorScreenPosX() + 6, ImGui.getCursorScreenPosY() + 4, ImGui.getColorU32(ImGuiCol.Text), "Grid");
        if (ImGui.button("## grid", 50, 20)) {
            showGrid = !showGrid;
        }

        drawList2.addText(ImGui.getCursorScreenPosX() + 6, ImGui.getCursorScreenPosY() + 4, ImGui.getColorU32(ImGuiCol.Text), "Show center");
        if (ImGui.button("## center", 50, 20)) {
            showCenter = !showCenter;
        }

        if (ImGui.button("Restart")) {
            gameData.setGameState(GameState.START);
        }

        if (ImGui.button("Win")) {
            gameData.getEventManager().addEvent(new GameWinEvent(null));
        }

        if (ImGui.button("Lose")) {
            gameData.getEventManager().addEvent(new GameLoseEvent(null));
        }

        ImGui.end();
    }
}
