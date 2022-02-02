package fr.hyriode.runner.listeners;

import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.hyrame.utils.Area;
import fr.hyriode.hyriapi.settings.HyriLanguage;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.externaltool.Cuboid;
import fr.hyriode.runner.game.HyriRunnerMessages;
import fr.hyriode.runner.inventories.HyriRunnerArrivedGui;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

public class HyriRunnerGameListener extends HyriListener<HyriRunner> {

    private static HashSet<UUID> arrived = new HashSet<>();
    public static boolean hasChosen;
    private int i = 1;

    public HyriRunnerGameListener(HyriRunner plugin) {
        super(plugin);
    }

    @EventHandler
    public void onEnterCenterZone(PlayerMoveEvent e) {
        Location pos1 = new Location(Bukkit.getWorld(plugin.getGameMap().getName()), -25, 256, -25);
        Location pos2 = new Location(Bukkit.getWorld(plugin.getGameMap().getName()), 25, 0, 25);
        Cuboid area = new Cuboid(pos1, pos2);
        Player player = e.getPlayer();
        if(area.contains(player.getLocation())) {
            if(!plugin.getGame().getDeadPlayers().contains(plugin.getGame().getPlayer(player.getUniqueId()))) {
                if(!arrived.contains(player.getUniqueId())) {
                    this.plugin.getGame().getPlayer(player.getUniqueId()).setPosition(i);
                    i += 1;
                    if(arrived.isEmpty()) {
                        arrived.add(player.getUniqueId());
                        HyriLanguageMessage guiName = new HyriLanguageMessage("gui.name")
                                .addValue(HyriLanguage.FR, ChatColor.DARK_AQUA+ "Bonus de première place")
                                .addValue(HyriLanguage.EN, ChatColor.DARK_AQUA+ "First place bonus");
                        HyriRunnerArrivedGui gui = new HyriRunnerArrivedGui(player, guiName.getForPlayer(player), plugin);
                        gui.open();
                        hasChosen = false;
                    } else {
                        arrived.add(player.getUniqueId());
                        Title.sendTitle(
                                player,
                                HyriRunnerMessages.ARRIVED_TITLE.get().getForPlayer(player),
                                HyriRunnerMessages.ARRIVED_SUB.get().getForPlayer(player)
                                        .replace("%position%", String.valueOf(plugin.getGame().getPlayer(player.getUniqueId()).getPosition())),
                                1,
                                4 * 20,
                                1
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
            if(plugin.getGame().getState().equals(HyriGameState.WAITING) || plugin.getGame().getState().equals(HyriGameState.READY) || plugin.getGame().getState().equals(HyriGameState.ENDED) || !plugin.getGame().isDamage()) {
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

    public static boolean isArrived(Player p) {
        return arrived.contains(p.getUniqueId());
    }
}
