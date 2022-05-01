package fr.hyriode.runner.game.scoreboard;

import fr.hyriode.runner.HyriRunner;
import org.bukkit.entity.Player;

public class RunnerSecondPhaseScoreboard extends RunnerScoreboard {
    public RunnerSecondPhaseScoreboard(HyriRunner plugin, Player player) {
        super(plugin, player);
    }

    @Override
    public void addLines() {
        this.addCurrentDateLine(0);
        this.addBlankLine(1);
        this.setLine(2, this.getKillsLine(), scoreboardLine -> scoreboardLine.setValue(this.getKillsLine()), 2);
        this.setLine(3, this.getAliveLine(), scoreboardLine -> scoreboardLine.setValue(this.getAliveLine()), 2);
        this.addBlankLine(4);
        this.addTimeLine();
        this.addBlankLine(6);

        this.addHostnameLine();
    }

    @Override
    public void addTimeLine() {
        this.setLine(5, this.getTimeLine());
    }

}
