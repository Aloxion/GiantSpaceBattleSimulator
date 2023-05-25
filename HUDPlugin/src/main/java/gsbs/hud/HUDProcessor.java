package gsbs.hud;

import gsbs.common.components.Health;
import gsbs.common.components.Position;
import gsbs.common.components.Team;
import gsbs.common.data.GameData;
import gsbs.common.data.World;
import gsbs.common.data.enums.Teams;
import gsbs.common.entities.Flagship;
import gsbs.common.math.Vector2;
import gsbs.common.services.ISystemPostProcess;
import gsbs.common.util.Color;

import static gsbs.common.util.Color.rgba;
import static org.lwjgl.nanovg.NanoVG.*;

public class HUDProcessor implements ISystemPostProcess {
    private Flagship getFlagshipFromTeam(Teams team, World world) {
        var flagships = world.getEntities(Flagship.class);
        var flagship = flagships.stream().filter(f -> f.getComponent(Team.class).getTeam().equals(team)).findFirst();
        return (Flagship) flagship.orElse(null);
    }

    private void drawHealthBar(GameData gameData, Vector2 position, Color color, int index, int health, int maxHealth) {
        var vg = gameData.getNvgContext();

        var topMargin = position.y - 20;
        var healthBarHeight = 8;
        var healthBarWidth = 40;

        nvgReset(gameData.getNvgContext());
        nvgBeginPath(vg);
        nvgRect(vg, position.x - healthBarWidth / 2f + 10, topMargin, healthBarWidth * health / (float) maxHealth, healthBarHeight);
        nvgFillColor(vg, rgba(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));
        nvgFill(vg);

        nvgBeginPath(vg);
        nvgRect(vg, position.x - healthBarWidth / 2f + 10, topMargin, healthBarWidth, healthBarHeight);
        nvgStrokeColor(vg, rgba(255, 255, 255, 255));
        nvgStroke(vg);
    }

    @Override
    public void process(GameData gameData, World world) {
        var player = getFlagshipFromTeam(Teams.PLAYER, world);
        var enemy = getFlagshipFromTeam(Teams.ENEMY, world);

        if (player == null || enemy == null)
            return;

        var playerPosition = player.getComponent(Position.class).asVector();
        var enemyPosition = enemy.getComponent(Position.class).asVector();

        var playerHealth = player.getComponent(Health.class);
        var playerHealthMax = playerHealth.getInitialHealth();
        var playerHealthCurrent = playerHealth.getHealthPoints();

        var enemyHealth = enemy.getComponent(Health.class);
        var enemyHealthMax = enemyHealth.getInitialHealth();
        var enemyHealthCurrent = enemyHealth.getHealthPoints();

        drawHealthBar(gameData, playerPosition, new Color(0, 0, 255, 255), 0, playerHealthCurrent, playerHealthMax);
        drawHealthBar(gameData, enemyPosition, new Color(255, 0, 0, 255), 1, enemyHealthCurrent, enemyHealthMax);
    }
}
