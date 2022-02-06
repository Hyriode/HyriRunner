package fr.hyriode.runner.listeners;

import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.game.HyriRunnerGame;
import fr.hyriode.runner.game.HyriRunnerGamePlayer;
import fr.hyriode.runner.inventories.HyriRunnerChallengeGui;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class HyriRunnerPlayerListener extends HyriListener<HyriRunner> {

    public HyriRunnerPlayerListener(HyriRunner plugin) {
        super(plugin);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if(!this.plugin.getGame().isDamage()) return;
        if (this.plugin.getGame().getState() == HyriGameState.PLAYING) {
            if (event.getEntity() instanceof Player) {
                final Player target = (Player) event.getEntity();
                final HyriRunnerGame game = this.plugin.getGame();
                final HyriRunnerGamePlayer gamePlayer = game.getPlayer(target.getUniqueId());

                if (event.getDamager() instanceof Player) {
                    gamePlayer.setLastHitter((Player) event.getDamager());
                } else if (event.getDamager() instanceof Projectile) {
                    final Projectile projectile = (Projectile) event.getDamager();

                    if (projectile.getShooter() instanceof Player) {
                        gamePlayer.setLastHitter((Player) projectile.getShooter());
                    }
                }

                if(target.getHealth() - event.getFinalDamage() <= 0) {
                    target.setHealth(20);

                    if (!game.getDeadPlayers().contains(gamePlayer)) {
                        gamePlayer.kill();
                    }
                }
            }
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!this.plugin.getGame().isDamage()) {
            event.setCancelled(true);
            return;
        }
        if (this.plugin.getGame().getState() == HyriGameState.PLAYING) {
            if (event.getEntity() instanceof Player) {
                final Player player = (Player) event.getEntity();
                final HyriRunnerGame game = this.plugin.getGame();
                final HyriRunnerGamePlayer gamePlayer = game.getPlayer(player.getUniqueId());

                if(player.getHealth() - event.getFinalDamage() <= 0) {
                    player.setHealth(20);

                    if (!game.getDeadPlayers().contains(gamePlayer)) {
                        gamePlayer.kill();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(!plugin.getGame().isCanPlace()){
            e.setCancelled(true);
        }
        if(plugin.getGame().getState().equals(HyriGameState.WAITING) || plugin.getGame().getState().equals(HyriGameState.READY)) {
            if(e.getItem() != null) {
                if(e.getItem().getType().equals(Material.PAPER)) {
                    new HyriRunnerChallengeGui(e.getPlayer(), plugin).open();
                }
            }
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
        if(plugin.getGame().getState().equals(HyriGameState.WAITING) || plugin.getGame().getState().equals(HyriGameState.READY)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPJoin(AsyncPlayerPreLoginEvent e) {
        if(!plugin.getGame().isAccessible()) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "The game map is generating...");
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if(plugin.getGame().getState().equals(HyriGameState.WAITING) || plugin.getGame().getState().equals(HyriGameState.READY)) {
            e.setCancelled(true);
        }
    }

}
