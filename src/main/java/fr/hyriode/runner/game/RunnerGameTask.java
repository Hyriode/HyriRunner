package fr.hyriode.runner.game;

import fr.hyriode.api.settings.HyriLanguage;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.game.scoreboard.RunnerSecondPhaseScoreboard;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RunnerGameTask extends BukkitRunnable {

    private float index;

    private final HyriRunner plugin;

    private final HyriLanguageMessage message = new HyriLanguageMessage("message.invincibility")
            .addValue(HyriLanguage.FR, ChatColor.RED + "Vous serez vulnérable dans %seconds% secondes !")
            .addValue(HyriLanguage.EN, ChatColor.RED + "You are going to be vulnerable in %seconds% seconds!");

    public RunnerGameTask(HyriRunner plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        final RunnerGame game = this.plugin.getGame();

        if (index == 0) {
            game.startBorderShrink();
            game.getPlayers().forEach(player -> player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.SILVERFISH_IDLE, 3f, 3f));
            game.sendMessageToAll(player -> RunnerMessage.BORDER_SHRINK.get().getForPlayer(player));
            game.sendMessageToAll(player -> message.getForPlayer(player).replace("%seconds%", String.valueOf(30)));
        }
        if (index == 10) {
            game.getPlayers().forEach(player -> player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.CLICK, 3f, 3f));
            game.sendMessageToAll(player -> message.getForPlayer(player).replace("%seconds%", String.valueOf(20)));
        }
        if (index == 27) {
            game.getPlayers().forEach(player -> player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.CLICK, 3f, 3f));
            game.sendMessageToAll(player -> message.getForPlayer(player).replace("%seconds%", String.valueOf(3)));
        }
        if (index == 28) {
            game.getPlayers().forEach(player -> player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.CLICK, 3f, 3f));
            game.sendMessageToAll(player -> message.getForPlayer(player).replace("%seconds%", String.valueOf(2)));
        }
        if (index == 29) {
            game.getPlayers().forEach(player -> player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.CLICK, 3f, 3f));
            game.sendMessageToAll(player -> message.getForPlayer(player)
                    .replace("%seconds%", String.valueOf(1))
                    .replace("secondes", "seconde")
                    .replace("seconds", "second")
            );
        }
        if (index == 30) {
            game.setDamage(true);
            game.sendMessageToAll(player -> RunnerMessage.DAMAGE_ON.get().getForPlayer(player));
            game.getPlayers().forEach(hyriRunnerGamePlayer -> {
                Player p = hyriRunnerGamePlayer.getPlayer();
                p.playSound(p.getLocation(), Sound.NOTE_PLING, 3f, 3f);
            });
        }
        if (game.isBorderEnd()) {
            game.setBorderEnd(false);
            game.sendMessageToAll(player -> RunnerMessage.BORDER_END.get().getForPlayer(player));
            game.getArrow().cancel();

            new BukkitRunnable() {
                private int primeIndex = 5;

                final HyriLanguageMessage pvpMessage = new HyriLanguageMessage("message.pvp-incoming")
                        .addValue(HyriLanguage.FR, ChatColor.RED + "Le pvp va s'activer dans %index% secondes !")
                        .addValue(HyriLanguage.EN, ChatColor.RED + "Le pvp will be enabled in %index% seconds!");

                @Override
                public void run() {
                    if(primeIndex > 0) {
                        game.getPlayers().forEach(player -> player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.CLICK, 3f, 3f));

                        if (primeIndex > 1) {
                            game.sendMessageToAll(player -> pvpMessage.getForPlayer(player).replace("%index%", String.valueOf(primeIndex)));
                        }
                        if (primeIndex == 1) {
                            game.sendMessageToAll(player -> pvpMessage.getForPlayer(player)
                                    .replace("%index%", String.valueOf(primeIndex))
                                    .replace("secondes", "seconde")
                                    .replace("seconds", "second"));
                        }
                    }
                    if (primeIndex == 0) {
                        game.setPvp(true);
                        game.setPlayersPvpPhaseRemaining(game.getPlayers().size() - 1);
                        game.sendMessageToAll(player -> RunnerMessage.PVP_ON.get().getForPlayer(player));
                        game.getPlayers().forEach(gamePlayer -> {
                            final Player p = gamePlayer.getPlayer();

                            gamePlayer.getScoreboard().hide();
                            gamePlayer.setScoreboard(new RunnerSecondPhaseScoreboard(plugin, p));
                            gamePlayer.getScoreboard().show();

                            p.playSound(p.getLocation(), Sound.WOLF_GROWL, 3f, 3f);
                        });
                        cancel();
                    }
                    primeIndex--;
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

    public float getIndex() {
        return this.index;
    }
}
