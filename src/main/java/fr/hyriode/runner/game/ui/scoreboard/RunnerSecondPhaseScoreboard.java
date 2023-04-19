package fr.hyriode.runner.game.ui.scoreboard;

import fr.hyriode.runner.HyriRunner;
import org.bukkit.entity.Player;

public class RunnerSecondPhaseScoreboard extends RunnerScoreboard {

    public RunnerSecondPhaseScoreboard(HyriRunner plugin, Player player) {
        super(plugin, player);

        this.addCurrentDateLine(0);
        this.addBlankLine(1);
        this.addBlankLine(4);
        this.addBlankLine(6);
        this.addHostnameLine();
    }

    @Override
    public void addUpdatableLines() {
        this.setLine(2, this.getKillsLine());
        this.setLine(3, this.getAliveLine());
    }

    @Override
    public void addTimeLine() {
        this.setLine(5, this.getTimeLine());
    }

}
