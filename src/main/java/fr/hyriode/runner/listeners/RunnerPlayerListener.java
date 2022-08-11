package fr.hyriode.runner.listeners;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.event.player.HyriGameDeathEvent;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.game.RunnerGamePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class RunnerPlayerListener extends HyriListener<HyriRunner> {

    public RunnerPlayerListener(HyriRunner plugin) {
        super(plugin);

        HyriAPI.get().getEventBus().register(this);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!plugin.getGame().isCanPlace()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        RunnerGamePlayer gamePlayer = plugin.getGame().getPlayer(player.getUniqueId());

        if(gamePlayer != null && !plugin.getGame().isPvp()) {
            gamePlayer.addBlockPlaced();
        }
    }

    @EventHandler
    public void onHeartRegen(EntityRegainHealthEvent e) {
        e.setCancelled(e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED));
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent e) {
        if (!e.getEntityType().equals(EntityType.DROPPED_ITEM)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onEat(FoodLevelChangeEvent e) {
        if (plugin.getGame().getState().equals(HyriGameState.WAITING) || plugin.getGame().getState().equals(HyriGameState.READY)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (plugin.getGame().getState().equals(HyriGameState.WAITING) || plugin.getGame().getState().equals(HyriGameState.READY)) {
            e.setCancelled(true);
        }
    }

}
