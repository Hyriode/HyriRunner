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
    // private int timeLine;

    public HyriRunnerScoreboard(HyriRunner plugin, Player player) {
        super(plugin, player, "therunner", ChatColor.DARK_AQUA + "     " + ChatColor.BOLD + plugin.getGame().getDisplayName() + "     ");
        this.plugin = plugin;
        // this.timeLine = timeLine;

        this.addLines();
    }

    public abstract void addLines();

    public void update() {
        this.addLines();

        this.updateLines();
    }

    public abstract void addTimeLine();

    public String getKillsLine() {
        long kills = this.plugin.getGame().getPlayer(this.player.getUniqueId()).getKills();
        return getLinePrefix("kills") + ChatColor.AQUA + kills;
    }

    public String getPositionLine() {
        int pos;
        if(!HyriRunnerGameListener.isArrived(player)) {
            pos = plugin.getGame().getPositionLead(plugin).indexOf(this.plugin.getGame().getPlayer(this.player.getUniqueId())) + 1;
        } else {
            pos = this.plugin.getGame().getPlayer(this.player.getUniqueId()).getPosition();
        }
        return getLinePrefix("position") + ChatColor.AQUA+ "#" +pos;
    }

    public String getBorderLine() {
        int size = (int) Bukkit.getWorld(plugin.getGameMap().getName()).getWorldBorder().getSize() / 2;
        return getLinePrefix("border") + ChatColor.AQUA + size + "m";
    }

    public String getAliveLine() {
        int alivePlayers = plugin.getGame().getPlayers().size() - plugin.getGame().getDeadPlayers().size();
        return getLinePrefix("alive") + ChatColor.AQUA + alivePlayers;
    }

    public String getCenterLine() {
        String prefix = this.getLinePrefix("centre");
        String distance = String.valueOf(this.plugin.getGame().getPlayer(this.player.getUniqueId()).getDistance());
        return prefix + ChatColor.AQUA + distance + "m";
    }

    public String getTimeLine() {
        final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        format.setTimeZone(TimeZone.getTimeZone("GMT"));

        final String line = format.format(this.plugin.getGame().getHyriRunnerGameTask().getIndex() * 1000);

        return this.getLinePrefix("time") + ChatColor.AQUA + (line.startsWith("00:") ? line.substring(3) : line);
    }
    public String getDateLine() {
        return ChatColor.GRAY + new SimpleDateFormat("dd/MsM/yyyy HH:mm").format(new Date());
    }

    public String getLinePrefix(String prefix) {
        return HyriRunner.getLanguageManager().getValue(this.player, "scoreboard." + prefix + ".display");
    }


}
