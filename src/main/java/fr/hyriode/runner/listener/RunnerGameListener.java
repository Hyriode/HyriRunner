package fr.hyriode.runner.listener;


import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.hyrame.utils.block.Cuboid;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.bonus.RunnerBonusGUI;
import fr.hyriode.runner.game.RunnerGame;
import fr.hyriode.runner.game.RunnerGamePlayer;
import fr.hyriode.runner.game.phase.RunnerPhase;
import fr.hyriode.runner.util.RunnerMessage;
import fr.hyriode.runner.util.RunnerValues;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class RunnerGameListener extends HyriListener<HyriRunner> {

    public RunnerGameListener(HyriRunner plugin) {
        super(plugin);
    }

    @EventHandler
    public void onEnterCenterZone(PlayerMoveEvent event) {
        final Location pos1 = new Location(Bukkit.getWorld(HyriRunner.GAME_MAP), -25, 256, -25);
        final Location pos2 = new Location(Bukkit.getWorld(HyriRunner.GAME_MAP), 25, 0, 25);
        final Cuboid area = new Cuboid(pos1, pos2);
        final Player player = event.getPlayer();
        final RunnerGame game = this.plugin.getGame();
        final RunnerGamePlayer gamePlayer = game.getPlayer(player.getUniqueId());

        if (gamePlayer == null) {
            return;
        }

        if (area.contains(player.getLocation())) {
            if (!gamePlayer.isDead() && !gamePlayer.isArrived()) {
                gamePlayer.setPosition(game.getArrivedPlayers().size() + 1);
                gamePlayer.setArrivedTime(plugin.getGame().getGameTask().getIndex());
                gamePlayer.setArrived();

                Title.sendTitle(player, RunnerMessage.ARRIVED_TITLE.asString(player), RunnerMessage.ARRIVED_SUB.asString(player).replace("%position%", String.valueOf(plugin.getGame().getPlayer(player.getUniqueId()).getPosition())), 5, 4 * 20, 5);

                if (game.getArrivedPlayers().size() == 1 && game.getState() == HyriGameState.PLAYING && RunnerValues.BONUS.get()) {
                    new RunnerBonusGUI(player, this.plugin).open();
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (!this.plugin.getGame().isPhase(RunnerPhase.DAMAGE) && !this.plugin.getGame().isPhase(RunnerPhase.PVP)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPvp(EntityDamageByEntityEvent event) {
        if (!this.plugin.getGame().isPhase(RunnerPhase.PVP)) {
            event.setDamage(0);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFoodLevelChanged(FoodLevelChangeEvent event) {
        if (RunnerValues.FOOD.get()) {
            event.setCancelled(false);
        }
    }

}
