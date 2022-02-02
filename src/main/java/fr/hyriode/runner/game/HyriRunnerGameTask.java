package fr.hyriode.runner.game;

import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyriapi.settings.HyriLanguage;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.game.scoreboard.HyriRunnerSecondPhaseScoreboard;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class HyriRunnerGameTask extends BukkitRunnable {

    private int index;
    private HyriRunner plugin;

    public HyriRunnerGameTask(HyriRunner plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {

        if (index == 0) {
            plugin.getGame().startBorderShrink();
            plugin.getGame().sendMessageToAll(player -> HyriRunnerMessages.BORDER_SHRINK.get().getForPlayer(player));
            plugin.getGame().getPlayers().forEach(hyriRunnerGamePlayer -> {
                Player p = hyriRunnerGamePlayer.getPlayer();
                p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 3f, 3f);
            });
        }
        if (index == 30) {
            plugin.getGame().setDamage(true);
            plugin.getGame().sendMessageToAll(player -> HyriRunnerMessages.DAMAGE_ON.get().getForPlayer(player));
        }
        if (plugin.getGame().isBorderEnd()) {
            plugin.getGame().setBorderEnd(false);
            plugin.getGame().sendMessageToAll(player -> HyriRunnerMessages.BORDER_END.get().getForPlayer(player));
            new BukkitRunnable() {
                private int index = 5;
                final HyriLanguageMessage pvpMessage = new HyriLanguageMessage("message.pvp-incoming")
                        .addValue(HyriLanguage.FR, ChatColor.RED + "Le pvp va s'activer dans %index% secondes !")
                        .addValue(HyriLanguage.EN, ChatColor.RED + "Le pvp will be enabled in %index% seconds!");

                @Override
                public void run() {
                    if (index > 1) {
                        plugin.getGame().sendMessageToAll(player -> pvpMessage.getForPlayer(player).replace("%index%", String.valueOf(index)));
                    }
                    if (index == 1) {
                        plugin.getGame().sendMessageToAll(player -> pvpMessage.getForPlayer(player)
                                .replace("%index%", String.valueOf(index))
                                .replace("secondes", "seconde")
                                .replace("seconds", "second"));
                    }
                    if (index == 0) {
                        plugin.getGame().setPvp(true);
                        plugin.getGame().sendMessageToAll(player -> HyriRunnerMessages.PVP_ON.get().getForPlayer(player));
                        plugin.getGame().getPlayers().forEach(hyriRunnerGamePlayer -> {
                            Player p = hyriRunnerGamePlayer.getPlayer();
                            hyriRunnerGamePlayer.getScoreboard().hide();
                            hyriRunnerGamePlayer.setScoreboard(new HyriRunnerSecondPhaseScoreboard(plugin, p));
                            hyriRunnerGamePlayer.getScoreboard().show();
                            p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 3f, 3f);
                        });
                        cancel();
                    }
                    index--;
                }
            }.runTaskTimer(plugin, 0, 20);
        }

        index++;
        plugin.getGame().getPlayers().forEach(player -> {
            if (player != null) {
                player.getScoreboard().addTimeLine();
            }
        });
    }

    public int getIndex() {
        return index;
    }
}
