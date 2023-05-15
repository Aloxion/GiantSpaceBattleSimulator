package gsbs.common.data;

import gsbs.common.events.Event;
import gsbs.common.events.EventManager;
import gsbs.common.services.IPlugin;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple data object, containing all the game data, that doesn't fit into the World.
 */
public class GameData {
    private final GameKeys keys = new GameKeys();
    private final EventManager eventManager = new EventManager();
    private final int nodeSize = 10;
    private long nvgContext;
    private List<IPlugin> initializedPlugins = new ArrayList<>();
    private GameState gameState = GameState.START;
    private float deltaTime;
    private int renderCycles;
    private List<Node> path;
    private Grid grid;

    public GameData(long nvgContext) {
        this.nvgContext = nvgContext;
    }

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public List<Node> getPath() {
        return path;
    }

    public void setPath(List<Node> path) {
        this.path = path;
    }

    /**
     * Get the number of milliseconds between frames.
     */
    public float getDeltaTime() {
        return deltaTime;
    }

    /**
     * Update the deltaTime i.e. the time in milliseconds between frames.
     */
    public void setDeltaTime(float deltaTime) {
        this.deltaTime = deltaTime;
    }

    public int getRenderCycles() {
        return renderCycles;
    }

    public void setRenderCycles(int renderCycles) {
        this.renderCycles = renderCycles;
    }

    /**
     * Get the width of the window in pixels.
     */
    public int getDisplayWidth() {
        return (int) ImGui.getIO().getDisplaySizeX();
    }

    /**
     * Get the height of the window in pixels.
     */
    public int getDisplayHeight() {
        return (int) ImGui.getIO().getDisplaySizeY();
    }

    /**
     * Return the game keys.
     */
    public GameKeys getKeys() {
        return keys;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public List<IPlugin> getInitializedPlugins() {
        return initializedPlugins;
    }

    public void setInitializedPlugins(List<IPlugin> initializedPlugins) {
        this.initializedPlugins = initializedPlugins;
    }

    /**
     * Add a new event to the event handler.
     */
    public void addEvent(Event e) {
        eventManager.addEvent(e);
    }

    /**
     * Get a list of all the events.
     */
    public EventManager getEventManager() {
        return eventManager;
    }

    public long getNvgContext() {
        return nvgContext;
    }

    public void setNvgContext(long nvgContext) {
        this.nvgContext = nvgContext;
    }

    public int getNodeSize() {
        return nodeSize;
    }
}
