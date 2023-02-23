package fr.hyriode.runner.listener;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameSpectator;
import fr.hyriode.hyrame.game.event.player.HyriGameReconnectEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameSpectatorEvent;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.game.RunnerGame;
import fr.hyriode.runner.game.phase.RunnerPhase;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class RunnerPlayerListener extends HyriListener<HyriRunner> {

    public RunnerPlayerListener(HyriRunner plugin) {
        super(plugin);

        HyriAPI.get().getEventBus().register(this);
    }

    @HyriEventHandler
    public void onSpectator(HyriGameSpectatorEvent event) {
        final HyriGameSpectator spectator = event.getSpectator();

        spectator.getPlayer().teleport(new Location(IHyrame.WORLD.get(), 0, 170, 0));

        if (spectator instanceof HyriGamePlayer) {
            final RunnerGame game = this.plugin.getGame();

            game.win(game.getWinner());
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
