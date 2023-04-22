package fr.hyriode.runner.game.ui.scoreboard;

import fr.hyriode.runner.HyriRunner;
import org.bukkit.entity.Player;

public class RunnerSpectatorScoreboard extends RunnerScoreboard {

    public RunnerSpectatorScoreboard(HyriRunner plugin, Player player) {
        super(plugin, player);

        this.addCurrentDateLine(0);
        this.addBlankLine(1);
        this.setLine(2, this.getBorderLine(), scoreboardLine -> scoreboardLine.setValue(this.getBorderLine()), 2);
        this.addBlankLine(3);
        this.addBlankLine(5);
        this.addBlankLine(7);

        this.addHostnameLine();
    }

    @Override
    public void addUpdatableLines() {
        this.setLine(4, this.getAliveLine());
    }

    @Override
    public void addTimeLine() {
        this.setLine(6, this.getTimeLine());
    }

}
