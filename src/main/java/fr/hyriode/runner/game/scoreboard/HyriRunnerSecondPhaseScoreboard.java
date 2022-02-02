package fr.hyriode.runner.game.scoreboard;

import fr.hyriode.hyrame.game.scoreboard.HyriScoreboardIpConsumer;
import fr.hyriode.runner.HyriRunner;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HyriRunnerSecondPhaseScoreboard extends HyriRunnerScoreboard{
    public HyriRunnerSecondPhaseScoreboard(HyriRunner plugin, Player player) {
        super(plugin, player);
    }

    @Override
    public void addLines() {
        this.setLine(0, this.getDateLine());
        this.setLine(1, "§a");
        this.setLine(2, this.getKillsLine(), scoreboardLine -> scoreboardLine.setValue(this.getKillsLine()), 2);
        this.setLine(3, this.getAliveLine(), scoreboardLine -> scoreboardLine.setValue(this.getAliveLine()), 2);
        this.setLine(4, "§b");
        // Line 5 in addTimeLine()
        this.setLine(6, "§c");
        this.setLine(7, ChatColor.DARK_AQUA + "hyriode.fr", new HyriScoreboardIpConsumer("hyriode.fr"), 2);
    }

    @Override
    public void addTimeLine() {
        this.setLine(5, this.getTimeLine());
    }
}
