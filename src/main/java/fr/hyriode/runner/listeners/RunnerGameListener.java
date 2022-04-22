package fr.hyriode.runner.listeners;

import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.api.settings.HyriLanguage;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.game.RunnerGame;
import fr.hyriode.runner.game.RunnerGamePlayer;
import fr.hyriode.runner.utils.Cuboid;
import fr.hyriode.runner.game.RunnerMessage;
import fr.hyriode.runner.inventories.RunnerArrivedGui;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class RunnerGameListener extends HyriListener<HyriRunner> {

    public static boolean hasChosen;
    private int i = 1;

    public RunnerGameListener(HyriRunner plugin) {
        super(plugin);
    }

    @EventHandler
    public void onEnterCenterZone(PlayerMoveEvent e) {
        final Location pos1 = new Location(Bukkit.getWorld(HyriRunner.GAME_MAP), -25, 256, -25);
        final Location pos2 = new Location(Bukkit.getWorld(HyriRunner.GAME_MAP), 25, 0, 25);
        final Cuboid area = new Cuboid(pos1, pos2);
        final Player player = e.getPlayer();
        final RunnerGame game = this.plugin.getGame();
        final RunnerGamePlayer gamePlayer = game.getPlayer(player.getUniqueId());

        if(game.getState() == HyriGameState.WAITING || game.getState() == HyriGameState.READY) {
            if(player.getLocation().getY() <= 120) {
                player.teleport(plugin.getConfiguration().getSpawn());
            }
        }

        if(area.contains(player.getLocation())) {
            if(!gamePlayer.isDead()) {
                if(!gamePlayer.isArrived()) {
                    gamePlayer.setPosition(i);
                    gamePlayer.setArrivedTime(plugin.getGame().getTimer().getCurrentTime());
                    i += 1;
                    if(game.getArrivedPlayers().isEmpty()) {
                        gamePlayer.setArrived(true);

                        final HyriLanguageMessage guiName = new HyriLanguageMessage("arrived.gui.name")
                                .addValue(HyriLanguage.FR, ChatColor.DARK_AQUA+ "Bonus d'arrivée")
                                .addValue(HyriLanguage.EN, ChatColor.DARK_AQUA+ "Arrival bonus");

                        final RunnerArrivedGui gui = new RunnerArrivedGui(player, guiName.getForPlayer(player), plugin);

                        gui.open();

                        hasChosen = false;
                    } else {
                        gamePlayer.setArrived(true);

                        Title.sendTitle(player, RunnerMessage.ARRIVED_TITLE.get().getForPlayer(player), RunnerMessage.ARRIVED_SUB.get().getForPlayer(player)
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
            if(!plugin.getGame().getState().equals(HyriGameState.PLAYING) || !plugin.getGame().isDamage()) {
                e.setDamage(0);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPvp(EntityDamageByEntityEvent e) {
        if(!plugin.getGame().isPvp()) {
            e.setDamage(0);
            e.setCancelled(true);
        }
    }

}
