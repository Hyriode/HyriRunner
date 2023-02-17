package fr.hyriode.runner.game;


import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.runner.util.RunnerMessage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class RunnerArrow extends BukkitRunnable {

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
        final World world = player.getWorld();

        final Location playerLocation = player.getLocation();

        playerLocation.setPitch(0);
        playerLocation.setY(0);

        final Vector direction = playerLocation.getDirection();
        final Vector loc = new Location(world, 0, 0, 0).subtract(playerLocation).toVector();
        final double angleLook = (Math.atan2(direction.getZ(), direction.getX()) / 2 / Math.PI * 360 + 360) % 360;
        final double angleDir = (Math.atan2(loc.getZ(), loc.getX()) / 2 / Math.PI * 360 + 360) % 360;
        final double angle = (angleDir - angleLook + 360) % 360;

        String result = "§l•";
        if (angle >= 337.5 && angle <= 360 || angle >= 0 && angle < 22.5) {
            result = "⬆";
        } else if (angle >= 22.5 && angle < 67.5) {
            result = "⬈";
        } else if (angle >= 67.5 && angle < 112.5) {
            result = "➡";
        } else if (angle >= 112.5 && angle < 157.5) {
            result = "⬊";
        } else if (angle >= 157.5 && angle < 202.5) {//180
            result = "⬇";
        } else if (angle >= 202.5 && angle < 247.5) {
            result = "⬋";
        } else if (angle >= 247.5 && angle < 292.5) {
            result = ChatColor.BOLD + "⬅";
        } else if (angle >= 292.5 && angle < 337.5) {
            result = "⬉";
        }

        new ActionBar(RunnerMessage.ARROW_BAR.asString(player)
                .replace("%arrow%", result)
                .replace("%meters%", String.valueOf(this.gamePlayer.getCenterDistance())))
                .send(player);
    }
}
