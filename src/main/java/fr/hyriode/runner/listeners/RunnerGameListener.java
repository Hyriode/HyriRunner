package fr.hyriode.runner.listeners;


import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.hyrame.utils.block.Cuboid;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.game.RunnerGame;
import fr.hyriode.runner.game.RunnerGamePlayer;
import fr.hyriode.runner.game.RunnerMessage;
import fr.hyriode.runner.inventories.RunnerArrivedGui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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

        if (gamePlayer == null) {
            return;
        }

        if(game.getState() == HyriGameState.WAITING || game.getState() == HyriGameState.READY) {
            if(player.getLocation().getY() <= this.plugin.getConfiguration().getSpawn().getY() - 10) {
                player.teleport(this.plugin.getConfiguration().getSpawn().asBukkit());
            }
        }

        if(area.contains(player.getLocation())) {
            if(!gamePlayer.isDead()) {
                if(!gamePlayer.isArrived()) {
                    gamePlayer.setPosition(i);
                    gamePlayer.setArrivedTime(plugin.getGame().getGameTask().getIndex());

                    i += 1;
                    if(game.getArrivedPlayers().isEmpty()) {
                        gamePlayer.setArrived();

                        final HyriLanguageMessage guiName = new HyriLanguageMessage("arrived.gui.name")
                                .addValue(HyriLanguage.FR, ChatColor.DARK_AQUA+ "Bonus d'arrivée")
                                .addValue(HyriLanguage.EN, ChatColor.DARK_AQUA+ "Arrival bonus");

                        final RunnerArrivedGui gui = new RunnerArrivedGui(player, guiName.getValue(player), plugin);

                        gui.open();

                        hasChosen = false;
                    } else {
                        gamePlayer.setArrived();

                        Title.sendTitle(player, RunnerMessage.ARRIVED_TITLE.asString(player), RunnerMessage.ARRIVED_SUB.asString(player)
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
            if(!this.plugin.getGame().getState().equals(HyriGameState.PLAYING) || !this.plugin.getGame().isDamage()) {
                e.setDamage(0);
                e.setCancelled(true);
            }

            if(this.plugin.getGame().getState().equals(HyriGameState.PLAYING)) {
                final Player player = (Player) e.getEntity();
                final RunnerGamePlayer gamePlayer = this.plugin.getGame().getPlayer(player.getUniqueId());

                if(gamePlayer == null) {
                    return;
                }

                if(!gamePlayer.isArrived() && !gamePlayer.isDamagesTaken()) {
                    gamePlayer.setDamagesTaken(true);
                }
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
