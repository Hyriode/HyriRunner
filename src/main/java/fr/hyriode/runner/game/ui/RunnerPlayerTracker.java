package fr.hyriode.runner.game.ui;

import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.game.RunnerGamePlayer;
import fr.hyriode.runner.util.LocationUtil;
import fr.hyriode.runner.util.RunnerMessage;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by AstFaster
 * on 23/03/2023 at 18:42
 */
public class RunnerPlayerTracker extends BukkitRunnable {

    private final RunnerGamePlayer gamePlayer;

    public RunnerPlayerTracker(RunnerGamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    @Override
    public void run() {
        if (this.gamePlayer == null || !this.gamePlayer.isOnline()) {
            return;
        }

        final Player player = this.gamePlayer.getPlayer();
        final NearestPlayer nearestPlayer = this.getNearestPlayer();

        if (nearestPlayer == null) {
            this.cancel();
            return;
        }

        new ActionBar(RunnerMessage.PLAYER_TRACKER_BAR.asString(player)
                .replace("%player%", nearestPlayer.asGamePlayer().getPlayer().getName())
                .replace("%arrow%", LocationUtil.getArrow(player.getLocation(), nearestPlayer.asGamePlayer().getPlayer().getLocation().clone()))
                .replace("%meters%", String.valueOf((int) nearestPlayer.getDistance())))
                .send(player);
    }

    private NearestPlayer getNearestPlayer() {
        final Location playerLocation = this.gamePlayer.getPlayer().getLocation().clone();

        playerLocation.setY(0.0D);

        NearestPlayer nearestPlayer = null;
        for (RunnerGamePlayer target : HyriRunner.get().getGame().getPlayers()) {
            if (!target.isOnline() || target.isSpectator() || this.gamePlayer.getTeam().contains(target)) {
                continue;
            }

            final Location targetLocation = target.getPlayer().getLocation().clone();

            if (!targetLocation.getWorld().equals(playerLocation.getWorld())) {
                continue;
            }

            targetLocation.setY(0.0D);

            final double distance = targetLocation.distance(playerLocation);

            if (nearestPlayer == null || distance < nearestPlayer.getDistance()) {
                nearestPlayer = new NearestPlayer(target, distance);
            }
        }
        return nearestPlayer;
    }

    public static class NearestPlayer {

        private final RunnerGamePlayer gamePlayer;
        private final double distance;

        public NearestPlayer(RunnerGamePlayer gamePlayer, double distance) {
            this.gamePlayer = gamePlayer;
            this.distance = distance;
        }

        public RunnerGamePlayer asGamePlayer() {
            return this.gamePlayer;
        }

        public double getDistance() {
            return this.distance;
        }

    }

}

