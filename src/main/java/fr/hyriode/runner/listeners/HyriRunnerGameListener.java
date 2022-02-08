package fr.hyriode.runner.listeners;

import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.hyriapi.settings.HyriLanguage;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.game.HyriRunnerGame;
import fr.hyriode.runner.game.HyriRunnerGamePlayer;
import fr.hyriode.runner.utils.Cuboid;
import fr.hyriode.runner.game.HyriRunnerMessages;
import fr.hyriode.runner.inventories.HyriRunnerArrivedGui;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashSet;
import java.util.UUID;

public class HyriRunnerGameListener extends HyriListener<HyriRunner> {

    public static boolean hasChosen;
    private int i = 1;

    public HyriRunnerGameListener(HyriRunner plugin) {
        super(plugin);
    }

    @EventHandler
    public void onEnterCenterZone(PlayerMoveEvent e) {
        final Location pos1 = new Location(Bukkit.getWorld(plugin.getGameMap().getName()), -25, 256, -25);
        final Location pos2 = new Location(Bukkit.getWorld(plugin.getGameMap().getName()), 25, 0, 25);
        final Cuboid area = new Cuboid(pos1, pos2);
        final Player player = e.getPlayer();
        final HyriRunnerGame game = this.plugin.getGame();
        final HyriRunnerGamePlayer gamePlayer = game.getPlayer(player.getUniqueId());

        if(area.contains(player.getLocation())) {
            if(!gamePlayer.isDead()) {
                if(!gamePlayer.isArrived()) {
                    gamePlayer.setPosition(i);
                    gamePlayer.setArrivedTime(plugin.getGame().getGameTask().getIndex());
                    i += 1;
                    if(game.getArrivedPlayers().isEmpty()) {
                        gamePlayer.setArrived(true);

                        final HyriLanguageMessage guiName = new HyriLanguageMessage("arrived.gui.name")
                                .addValue(HyriLanguage.FR, ChatColor.DARK_AQUA+ "Bonus d'arrivée")
                                .addValue(HyriLanguage.EN, ChatColor.DARK_AQUA+ "Arrival bonus");

                        final HyriRunnerArrivedGui gui = new HyriRunnerArrivedGui(player, guiName.getForPlayer(player), plugin);

                        gui.open();

                        hasChosen = false;
                    } else {
                        gamePlayer.setArrived(true);

                        Title.sendTitle(player, HyriRunnerMessages.ARRIVED_TITLE.get().getForPlayer(player), HyriRunnerMessages.ARRIVED_SUB.get().getForPlayer(player)
                                        .replace("%position%", String.valueOf(plugin.getGame().getPlayer(player.getUniqueId()).getPosition())), 1, 4 * 20, 1
                        );
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if(!plugin.getGame().getState().equals(HyriGameState.PLAYING) || !plugin.getGame().isDamage()) {

                e.setCancelled(true);

                if(e.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
                    p.teleport(plugin.getConfiguration().getSpawn());
                }
            }
        }
    }

    @EventHandler
    public void onPvp(EntityDamageByEntityEvent e) {
        if(!plugin.getGame().isPvp()) {
            e.setCancelled(true);
        }
    }

}
