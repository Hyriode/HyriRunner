package fr.hyriode.runner.game;

import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyriapi.settings.HyriLanguage;
import fr.hyriode.runner.HyriRunner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HyriRunnerArrow {
    private ScheduledExecutorService scheduledExecutorService;
    private HyriRunner plugin;
    private static HyriLanguageMessage barMessage = new HyriLanguageMessage("actionbar.arrow")
            .addValue(HyriLanguage.FR, ChatColor.DARK_AQUA + "Direction vers le centre : " + ChatColor.AQUA + "" + ChatColor.BOLD + "%arrow%")
            .addValue(HyriLanguage.EN, ChatColor.DARK_AQUA + "Direction to the center: " + ChatColor.AQUA + "" + ChatColor.BOLD + "%arrow%");

    public HyriRunnerArrow(HyriRunner plugin) {
        super();
        this.plugin = plugin;
        scheduledExecutorService = Executors.newScheduledThreadPool(4);
    }

    public void schedule() {
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    World world = player.getWorld();

                    Location l1 = player.getLocation();
                    l1.setPitch(0);
                    l1.setY(0);
                    Vector direction = l1.getDirection();
                    Location l2 = new Location(world, 0, 0, 0);
                    l2.setY(0);
                    Vector loc = l2.subtract(l1).toVector();


                    StringBuilder sb = new StringBuilder();
                    String c = "§l•";
                    double angleLook = (Math.atan2(direction.getZ(), direction.getX()) / 2 / Math.PI * 360 + 360) % 360;
                    double angleDir = (Math.atan2(loc.getZ(), loc.getX()) / 2 / Math.PI * 360 + 360) % 360;

                    double angle = (angleDir - angleLook + 360) % 360;

                    if (angle >= 337.5 && angle <= 360 || angle >= 0 && angle < 22.5) {
                        c = "⬆";
                    } else {
                        if (angle >= 22.5 && angle < 67.5) {
                            c = "⬈";
                        } else {
                            if (angle >= 67.5 && angle < 112.5) {
                                c = "➡";
                            } else {
                                if (angle >= 112.5 && angle < 157.5) {
                                    c = "⬊";
                                } else {
                                    if (angle >= 157.5 && angle < 202.5) {//180
                                        c = "⬇";
                                    } else {
                                        if (angle >= 202.5 && angle < 247.5) {
                                            c = "⬋";
                                        } else {
                                            if (angle >= 247.5 && angle < 292.5) {
                                                c = "§l⬅";
                                            } else {
                                                if (angle >= 292.5 && angle < 337.5) {
                                                    c = "⬉";
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
                    sb.append(c);
                    ActionBar bar = new ActionBar(barMessage.getForPlayer(player)
                            .replace("%arrow%", sb.toString()));
                    bar.send(player);
                }
            }
        }, 0, 350, TimeUnit.MILLISECONDS);
    }
}
