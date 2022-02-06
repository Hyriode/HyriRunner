package fr.hyriode.runner.game.scoreboard;

import fr.hyriode.hyrame.scoreboard.Scoreboard;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.game.HyriRunnerGamePlayer;
import fr.hyriode.runner.listeners.HyriRunnerGameListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public abstract class HyriRunnerScoreboard extends Scoreboard {

    private final HyriRunner plugin;

    public HyriRunnerScoreboard(HyriRunner plugin, Player player) {
        super(plugin, player, "therunner", ChatColor.DARK_AQUA + "     " + ChatColor.BOLD + plugin.getGame().getDisplayName() + "     ");
        this.plugin = plugin;

        this.addLines();
    }

    public abstract void addLines();

    public void update() {
        this.addLines();

        this.updateLines();
    }

    public abstract void addTimeLine();

    protected String getKillsLine() {
        return getLinePrefix("kills") + ChatColor.AQUA + this.getGamePlayer().getKills();
    }

    protected String getPositionLine() {
        int pos;
        if(!this.getGamePlayer().isArrived()) {
            pos = plugin.getGame().getPositionLead().indexOf(this.getGamePlayer()) + 1;
        } else {
            pos = this.getGamePlayer().getPosition();
        }
        return getLinePrefix("position") + ChatColor.AQUA+ "#" +pos;
    }

    protected String getBorderLine() {
        int size = (int) Bukkit.getWorld(plugin.getGameMap().getName()).getWorldBorder().getSize() / 2;
        return getLinePrefix("border") + ChatColor.AQUA + size + "m";
    }

    protected String getAliveLine() {
        int alivePlayers = plugin.getGame().getPlayers().size() - plugin.getGame().getDeadPlayers().size();
        return getLinePrefix("alive") + ChatColor.AQUA + alivePlayers;
    }

    protected String getCenterLine() {
        String prefix = this.getLinePrefix("centre");
        String distance = String.valueOf(this.getGamePlayer().getDistance());
        return prefix + ChatColor.AQUA + distance + "m";
    }

    protected String getTimeLine() {
        final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        format.setTimeZone(TimeZone.getTimeZone("GMT"));

        final String line = format.format(this.plugin.getGame().getGameTask().getIndex() * 1000);

        return this.getLinePrefix("time") + ChatColor.AQUA + (line.startsWith("00:") ? line.substring(3) : line);
    }

    protected String getDateLine() {
        return ChatColor.GRAY + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
    }

    protected String getLinePrefix(String prefix) {
        return HyriRunner.getLanguageManager().getValue(this.player, "scoreboard." + prefix + ".display");
    }

    private HyriRunnerGamePlayer getGamePlayer() {
        return this.plugin.getGame().getPlayer(this.player.getUniqueId());
    }

}
