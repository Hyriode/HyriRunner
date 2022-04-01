package fr.hyriode.runner.game.scoreboard;

import fr.hyriode.hyrame.game.scoreboard.HyriScoreboardIpConsumer;
import fr.hyriode.runner.HyriRunner;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HyriRunnerFirstPhaseScoreboard extends HyriRunnerScoreboard {
    public HyriRunnerFirstPhaseScoreboard(HyriRunner plugin, Player player) {
        super(plugin, player);
    }

    @Override
    public void addLines() {
        this.setLine(0, this.getDateLine());
        this.addBlankLine(1);
        this.setLine(2, this.getCenterLine(), scoreboardLine -> scoreboardLine.setValue(this.getCenterLine()), 2);
        this.setLine(3, this.getBorderLine(), scoreboardLine -> scoreboardLine.setValue(this.getBorderLine()), 2);
        this.addBlankLine(4);
        this.setLine(5, this.getPositionLine(), scoreboardLine -> scoreboardLine.setValue(this.getPositionLine()), 5);
        this.setLine(6, this.getAliveLine(), scoreboardLine -> scoreboardLine.setValue(this.getAliveLine()), 20 * 2);
        this.addBlankLine(7);
        // 8 time line
        this.addBlankLine(9);

        this.addHostnameLine();
    }

    @Override
    public void addTimeLine() {
        this.setLine(8, this.getTimeLine());
    }

}
