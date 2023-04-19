package fr.hyriode.runner.game.ui;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.runner.game.RunnerGamePlayer;
import fr.hyriode.runner.util.LocationUtil;
import fr.hyriode.runner.util.RunnerMessage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class RunnerArrow extends BukkitRunnable {

    private final Location center = new Location(IHyrame.WORLD.get(), 0, 0, 0);

    private final RunnerGamePlayer gamePlayer;

    public RunnerArrow(RunnerGamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    @Override
    public void run() {
        if (this.gamePlayer == null || !this.gamePlayer.isOnline()) {
            return;
        }

        final Player player = this.gamePlayer.getPlayer();
        final Location playerLocation = player.getLocation().clone();

        if (!playerLocation.getWorld().equals(center.getWorld())) {
            return;
        }

        playerLocation.setPitch(0);
        playerLocation.setY(0);

        new ActionBar(RunnerMessage.ARROW_BAR.asString(player)
                .replace("%arrow%", LocationUtil.getArrow(playerLocation, this.center.clone()))
                .replace("%meters%", String.valueOf(this.gamePlayer.getCenterDistance())))
                .send(player);
    }

}
