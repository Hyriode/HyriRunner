package fr.hyriode.runner.game.ui.scoreboard;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.scoreboard.HyriGameScoreboard;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.game.RunnerGame;
import fr.hyriode.runner.game.RunnerGamePlayer;
import fr.hyriode.runner.game.RunnerGameTask;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public abstract class RunnerScoreboard extends HyriGameScoreboard<RunnerGame> {

    private final HyriRunner plugin;

    public RunnerScoreboard(HyriRunner plugin, Player player) {
        super(plugin, plugin.getGame(),player, plugin.getGame().getDisplayName());
        this.plugin = plugin;

        this.addUpdatableLines();
        this.addTimeLine();
    }

    public abstract void addUpdatableLines();

    public abstract void addTimeLine();

    public void update() {
        this.addUpdatableLines();

        this.updateLines();
    }

    protected String getKillsLine() {
        return this.getLinePrefix("kills") + ChatColor.AQUA + this.getGamePlayer().getKills();
    }

    protected String getPositionLine() {
        int pos;
        if(!this.getGamePlayer().isArrived()) {
            pos = this.plugin.getGame().getPlayersLeaderboard().indexOf(this.getGamePlayer()) + 1;
        } else {
            pos = this.getGamePlayer().getPosition();
        }
        return this.getLinePrefix("position") + ChatColor.AQUA+ "#" +pos;
    }

    protected String getBorderLine() {
        final int size = (int) IHyrame.WORLD.get().getWorldBorder().getSize() / 2;

        return this.getLinePrefix("border") + ChatColor.AQUA + size + "m";
    }

    protected String getAliveLine() {
        final int alivePlayers = plugin.getGame().getPlayers().size() - plugin.getGame().getDeadPlayers().size();

        return this.getLinePrefix("alive") + ChatColor.AQUA + alivePlayers;
    }

    protected String getCenterLine() {
        final String distance = String.valueOf(this.getGamePlayer().getCenterDistance());

        return this.getLinePrefix("center") + ChatColor.AQUA + distance + "m";
    }

    protected String getTimeLine() {
        final RunnerGameTask task = this.plugin.getGame().getGameTask();
        final int index = task == null ? 0 : task.getIndex();
        final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        format.setTimeZone(TimeZone.getTimeZone("GMT"));

        final String line = format.format(index * 1000);

        return this.getLinePrefix("time") + ChatColor.AQUA + (index == 0 ? "00:00" : (line.startsWith("00:") ? line.substring(3) : line));
    }

    protected String getLinePrefix(String prefix) {
        return HyriLanguageMessage.get("scoreboard." + prefix + ".display").getValue(this.player);
    }

    protected RunnerGamePlayer getGamePlayer() {
        return this.plugin.getGame().getPlayer(this.player.getUniqueId());
    }

}
