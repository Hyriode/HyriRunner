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

    private final HyriRunner plugin;

    public HyriRunnerGameTask(HyriRunner plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        final HyriRunnerGame game = this.plugin.getGame();

        if (index == 0) {
            game.startBorderShrink();
            game.sendMessageToAll(player -> HyriRunnerMessages.BORDER_SHRINK.get().getForPlayer(player));
            game.getPlayers().forEach(hyriRunnerGamePlayer -> {
                Player p = hyriRunnerGamePlayer.getPlayer();
                p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 3f, 3f);
            });
        }
        if (index == 30) {
            game.setDamage(true);
            game.sendMessageToAll(player -> HyriRunnerMessages.DAMAGE_ON.get().getForPlayer(player));
        }
        if (game.isBorderEnd()) {
            game.setBorderEnd(false);
            game.sendMessageToAll(player -> HyriRunnerMessages.BORDER_END.get().getForPlayer(player));

            new BukkitRunnable() {
                private int index = 5;

                final HyriLanguageMessage pvpMessage = new HyriLanguageMessage("message.pvp-incoming")
                        .addValue(HyriLanguage.FR, ChatColor.RED + "Le pvp va s'activer dans %index% secondes !")
                        .addValue(HyriLanguage.EN, ChatColor.RED + "Le pvp will be enabled in %index% seconds!");

                @Override
                public void run() {
                    if (index > 1) {
                        game.sendMessageToAll(player -> pvpMessage.getForPlayer(player).replace("%index%", String.valueOf(index)));
                    }
                    if (index == 1) {
                        game.sendMessageToAll(player -> pvpMessage.getForPlayer(player)
                                .replace("%index%", String.valueOf(index))
                                .replace("secondes", "seconde")
                                .replace("seconds", "second"));
                    }
                    if (index == 0) {
                        game.setPvp(true);
                        game.sendMessageToAll(player -> HyriRunnerMessages.PVP_ON.get().getForPlayer(player));
                        game.getPlayers().forEach(gamePlayer -> {
                            final Player p = gamePlayer.getPlayer();

                            gamePlayer.getScoreboard().hide();
                            gamePlayer.setScoreboard(new HyriRunnerSecondPhaseScoreboard(plugin, p));
                            gamePlayer.getScoreboard().show();

                            p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 3f, 3f);
                        });
                        cancel();
                    }
                    index--;
                }
            }.runTaskTimer(plugin, 0, 20);
        }

        index++;

        game.getPlayers().forEach(player -> {
            if (player != null) {
                if (player.getScoreboard() != null) {
                    player.getScoreboard().addTimeLine();
                }
            }
        });
    }

    public int getIndex() {
        return index;
    }
}
