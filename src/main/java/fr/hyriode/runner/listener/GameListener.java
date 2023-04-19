package fr.hyriode.runner.listener;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.api.leaderboard.HyriLeaderboardScope;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameSpectator;
import fr.hyriode.hyrame.game.event.player.HyriGameReconnectEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameSpectatorEvent;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.hyrame.scoreboard.HyriScoreboard;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.game.RunnerGame;
import fr.hyriode.runner.game.phase.RunnerPhase;
import fr.hyriode.runner.game.phase.RunnerPhaseTriggeredEvent;
import fr.hyriode.runner.game.ui.scoreboard.RunnerFirstPhaseScoreboard;
import fr.hyriode.runner.game.ui.scoreboard.RunnerSecondPhaseScoreboard;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class GameListener extends HyriListener<HyriRunner> {

    public GameListener(HyriRunner plugin) {
        super(plugin);

        HyriAPI.get().getEventBus().register(this);
    }

    @HyriEventHandler
    public void onSpectator(HyriGameSpectatorEvent event) {
        final HyriGameSpectator spectator = event.getSpectator();
        final Player player = spectator.getPlayer();

        player.teleport(new Location(IHyrame.WORLD.get(), 0, 170, 0));

        if (spectator instanceof HyriGamePlayer) {
            final RunnerGame game = this.plugin.getGame();

            game.win(game.getWinner());
        } else {
            final RunnerGame game = event.getGame().cast();

            if (game.isPhase(RunnerPhase.PVP)) {
                new RunnerSecondPhaseScoreboard(this.plugin, player).show();
            } else {
                new RunnerFirstPhaseScoreboard(this.plugin, player).show();
            }
        }
    }

    @HyriEventHandler
    public void onPhaseTriggered(RunnerPhaseTriggeredEvent event) {
        final RunnerGame game = event.getGame();
        final RunnerPhase phase = event.getPhase();

        if (phase == RunnerPhase.PVP) {
            for (HyriGameSpectator spectator : game.getSpectators()) {
                final Player player = spectator.getPlayer();
                final HyriScoreboard oldScoreboard = IHyrame.get().getScoreboardManager().getPlayerScoreboard(player);

                if (oldScoreboard != null) {
                    oldScoreboard.hide();
                }

                new RunnerSecondPhaseScoreboard(this.plugin, player).show();
            }
        }
    }

    @HyriEventHandler
    public void onReconnect(HyriGameReconnectEvent event) {
        final RunnerGame game = this.plugin.getGame();

        if (game.isPhase(RunnerPhase.BORDER_END)) {
            event.disallow();
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!this.plugin.getGame().isPhase(RunnerPhase.PLACE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHeartRegen(EntityRegainHealthEvent event) {
        event.setCancelled(event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED));
    }

}
