package fr.hyriode.runner.game;


import fr.hyriode.hyrame.utils.BroadcastUtil;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.game.phase.RunnerPhase;
import fr.hyriode.runner.game.scoreboard.RunnerScoreboard;
import fr.hyriode.runner.game.scoreboard.RunnerSecondPhaseScoreboard;
import fr.hyriode.runner.util.RunnerMessage;
import fr.hyriode.runner.util.RunnerValues;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RunnerGameTask extends BukkitRunnable {

    private boolean borderTriggered;

    private int index;

    private final HyriRunner plugin;

    public RunnerGameTask(HyriRunner plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        final RunnerGame game = this.plugin.getGame();
        final boolean invincibility = RunnerValues.INVINCIBILITY.get();

        if (this.index == 0) {
            Bukkit.getScheduler().runTaskLater(this.plugin, () ->  {
                game.startBorderShrink();
                game.getPlayers().forEach(player -> player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.SILVERFISH_IDLE, 3f, 3f));
                BroadcastUtil.broadcast(RunnerMessage.BORDER_SHRINK::asString);
            }, RunnerValues.BORDER_TIME.get() * 20L);

            if (!invincibility) {
                BroadcastUtil.broadcast(player -> RunnerMessage.INVINCIBILITY.asString(player).replace("%seconds%", String.valueOf(30)));
            }
        } else if (this.index == 10 || this.index == 27 || this.index == 28 || this.index == 29) {
            game.getPlayers().forEach(player -> player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.CLICK, 3f, 3f));

            if (!invincibility) {
                BroadcastUtil.broadcast(player -> RunnerMessage.INVINCIBILITY.asString(player).replace("%seconds%", String.valueOf(30 - this.index)));
            }
        } else if (this.index == 30 && !invincibility) {
            game.triggerPhase(RunnerPhase.DAMAGE);

            BroadcastUtil.broadcast(RunnerMessage.DAMAGE_ON::asString);
            game.getPlayers().forEach(gamePlayer -> {
                final Player player = gamePlayer.getPlayer();

                player.playSound(player.getLocation(), Sound.NOTE_PLING, 3f, 3f);
            });
        }

        // Stop au bout de 10 minutes
        if (this.index == RunnerValues.GAME_TIME.get()) {
            game.win(game.getBestTeam());
        }

        if (game.isPhase(RunnerPhase.BORDER_END) && !this.borderTriggered) {
            if (!invincibility) {
                game.triggerPhase(RunnerPhase.DAMAGE);
            }

            this.borderTriggered = true;

            BroadcastUtil.broadcast(RunnerMessage.BORDER_END::asString);
            game.getArrow().cancel();

            new BukkitRunnable() {

                private int index = 5;

                @Override
                public void run() {
                    if (this.index > 0) {
                        game.getPlayers().forEach(player -> player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.CLICK, 3f, 3f));
                        BroadcastUtil.broadcast(player -> RunnerMessage.PVP_INCOMING.asString(player).replace("%seconds%", String.valueOf(this.index)));
                    }

                    if (this.index == 0) {
                        game.triggerPhase(RunnerPhase.PVP);
                        BroadcastUtil.broadcast(RunnerMessage.PVP_ON::asString);
                        game.getPlayers().forEach(gamePlayer -> {
                            final Player player = gamePlayer.getPlayer();
                            final RunnerScoreboard scoreboard = new RunnerSecondPhaseScoreboard(plugin, player);

                            gamePlayer.getScoreboard().hide();
                            gamePlayer.setScoreboard(scoreboard);
                            scoreboard.show();

                            player.playSound(player.getLocation(), Sound.WOLF_GROWL, 3f, 3f);
                        });

                        this.cancel();
                    }
                    this.index--;
                }
            }.runTaskTimer(plugin, 0, 20);
        }

        game.getPlayers().forEach(gamePlayer -> {
            gamePlayer.getScoreboard().addTimeLine();
            if (this.isOutsideBorder(gamePlayer.getPlayer().getLocation())) {
                gamePlayer.getPlayer().damage(2.0);
            }
        });

        this.index++;
    }

    public int getIndex() {
        return this.index;
    }

    private boolean isOutsideBorder(Location location) {
        final double borderCoordinate = location.getWorld().getWorldBorder().getSize()/2;

        if (location.getY() < 40 || location.getY() > 95) {
            return true;
        }

        if (location.getX() > borderCoordinate || location.getX() < -borderCoordinate
                || location.getZ() > borderCoordinate || location.getZ() < -borderCoordinate) {
            return true;
        }
        return false;
    }
}
