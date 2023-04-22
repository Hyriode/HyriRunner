package fr.hyriode.runner.listener;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.api.leaderboard.HyriLeaderboardScope;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameSpectator;
import fr.hyriode.hyrame.game.event.player.HyriGameReconnectEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameReconnectedEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameSpectatorEvent;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.hyrame.scoreboard.HyriScoreboard;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.game.RunnerGame;
import fr.hyriode.runner.game.RunnerGamePlayer;
import fr.hyriode.runner.game.phase.RunnerPhase;
import fr.hyriode.runner.game.phase.RunnerPhaseTriggeredEvent;
import fr.hyriode.runner.game.ui.scoreboard.RunnerFirstPhaseScoreboard;
import fr.hyriode.runner.game.ui.scoreboard.RunnerSecondPhaseScoreboard;
import fr.hyriode.runner.game.ui.scoreboard.RunnerSpectatorScoreboard;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
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

        player.teleport(new Location(IHyrame.WORLD.get(), 0, 190, 0));

        if (spectator instanceof HyriGamePlayer) {
            final RunnerGame game = this.plugin.getGame();

            game.win(game.getWinner());
        } else {
            new RunnerSpectatorScoreboard(this.plugin, player).show();
        }
    }

    @HyriEventHandler
    public void onReconnect(HyriGameReconnectEvent event) {
        final RunnerGame game = this.plugin.getGame();

        if (game.isPhase(RunnerPhase.BORDER_END) || game.isPhase(RunnerPhase.PVP)) {
            event.disallow();
        }
    }

    @HyriEventHandler
    public void onReconnected(HyriGameReconnectedEvent event) {
        final RunnerGame game = this.plugin.getGame();
        final RunnerGamePlayer gamePlayer = event.getGamePlayer().cast();

        gamePlayer.onReconnect();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!this.plugin.getGame().isPhase(RunnerPhase.PLACE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHeartRegen(EntityRegainHealthEvent event) {
        final Entity entity = event.getEntity();

        if (entity instanceof Player) {
            final RunnerGamePlayer gamePlayer = this.plugin.getGame().getPlayer((Player) entity);

            if (gamePlayer != null && !gamePlayer.isSpectator()) {
                event.setCancelled(event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED));
            }
        }
    }

}
