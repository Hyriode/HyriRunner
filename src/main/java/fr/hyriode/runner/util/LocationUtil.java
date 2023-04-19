package fr.hyriode.runner.util;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * Created by AstFaster
 * on 23/03/2023 at 19:45
 */
public class LocationUtil {

    public static String getArrow(Location from, Location to) {
        final Vector direction = from.getDirection();
        final Vector loc = to.subtract(from).toVector();
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
        } else if (angle >= 157.5 && angle < 202.5) { //180
            result = "⬇";
        } else if (angle >= 202.5 && angle < 247.5) {
            result = "⬋";
        } else if (angle >= 247.5 && angle < 292.5) {
            result = ChatColor.BOLD + "⬅";
        } else if (angle >= 292.5 && angle < 337.5) {
            result = "⬉";
        }
        return result;
    }

}
