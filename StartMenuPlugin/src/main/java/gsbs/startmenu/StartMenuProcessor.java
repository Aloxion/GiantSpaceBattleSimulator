package gsbs.startmenu;

import gsbs.common.data.GameData;
import gsbs.common.data.GameState;
import gsbs.common.data.World;
import gsbs.common.events.Event;
import gsbs.common.events.GameLoseEvent;
import gsbs.common.events.GameWinEvent;
import gsbs.common.services.IEventListener;
import gsbs.common.services.ISystemProcess;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.ImGui;

import static org.lwjgl.nanovg.NanoVG.*;

public class StartMenuProcessor implements ISystemProcess, IEventListener {
    private static String gameResult = "";

    @Override
    public void process(GameData gameData, World world) {
        if (gameData.getGameState() != GameState.START) {
            return;
        }

        float windowWidth = gameData.getDisplayWidth() * 0.3f;

        var vg = gameData.getNvgContext();
        nvgReset(vg);
        nvgBeginPath(vg);
        nvgFontSize(vg, 50);
        nvgTextAlign(vg, NVG_ALIGN_CENTER | NVG_ALIGN_BOTTOM);
        nvgText(vg, gameData.getDisplayWidth() / 2.f, 150, "Giant Space Battle Simulator (GSBS)");


        ImGui.setNextWindowSize(windowWidth, gameData.getDisplayHeight());
        ImGui.setNextWindowPos((float) (gameData.getDisplayWidth() * 0.5 - windowWidth * 0.5), 0);


        ImGui.begin("Start Menu", ImGuiWindowFlags.NoBackground | ImGuiWindowFlags.NoTitleBar);

        ImGui.dummy(0, gameData.getDisplayHeight() * 0.3f);


        ImGui.text(gameResult);

        if (centeredButton("Start Game")) {
            gameData.setGameState(GameState.IN_GAME);
        }


        if (centeredButton("Quit")) {
            gameData.setGameState(GameState.QUIT);
        }

        ImGui.end();
    }

    private boolean centeredButton(String label) {
        float windowWidth = ImGui.getWindowSize().x;
        float textWidth = ImGui.calcTextSize(label).x;

        ImGui.setCursorPosX((windowWidth - textWidth) * 0.5f);

        return ImGui.button(label);
    }

    @Override
    public void onEvent(Event event, GameData gameData) {
        if (event instanceof GameWinEvent) {
            gameResult = "You have won!!";
        } else if (event instanceof GameLoseEvent) {
            gameResult = "You have lost!!";
        }
    }
}
