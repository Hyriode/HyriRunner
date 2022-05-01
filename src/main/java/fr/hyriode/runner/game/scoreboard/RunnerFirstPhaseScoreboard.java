package fr.hyriode.runner.game.scoreboard;

import fr.hyriode.runner.HyriRunner;
import org.bukkit.entity.Player;

public class RunnerFirstPhaseScoreboard extends RunnerScoreboard {

    public RunnerFirstPhaseScoreboard(HyriRunner plugin, Player player) {
        super(plugin, player);
    }

    @Override
    public void addLines() {
        this.addCurrentDateLine(0);
        this.addBlankLine(1);
        this.setLine(2, this.getCenterLine(), scoreboardLine -> scoreboardLine.setValue(this.getCenterLine()), 2);
        this.setLine(3, this.getBorderLine(), scoreboardLine -> scoreboardLine.setValue(this.getBorderLine()), 2);
        this.addBlankLine(4);
        this.setLine(5, this.getPositionLine(), scoreboardLine -> scoreboardLine.setValue(this.getPositionLine()), 5);
        this.setLine(6, this.getAliveLine(), scoreboardLine -> scoreboardLine.setValue(this.getAliveLine()), 20 * 2);
        this.addBlankLine(7);
        this.addBlankLine(8);
        this.addBlankLine(9);

        this.addHostnameLine();
    }

    @Override
    public void addTimeLine() {
        this.setLine(8, this.getTimeLine());
    }

}
