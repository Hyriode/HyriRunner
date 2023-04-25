package fr.hyriode.runner.challenge.model;

import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.RunnerChallengeModel;
import fr.hyriode.runner.challenge.RunnerChallenge;
import fr.hyriode.runner.challenge.RunnerChallengeDifficulty;
import fr.hyriode.runner.game.RunnerGamePlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RunnerLooterChallenge extends RunnerChallenge implements Listener {

    private final HyriRunner plugin;

    private final Set<UUID> looters;
    private final Set<Location> placedChestsLocations;

    public RunnerLooterChallenge(HyriRunner pl) {
        super(RunnerChallengeModel.LOOTER, "looter", Material.CHEST, RunnerChallengeDifficulty.EXTREME);
        this.plugin = pl;
        this.looters = new HashSet<>();
        this.placedChestsLocations = new HashSet<>();

        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @Override
    public boolean isValid(RunnerGamePlayer gamePlayer) {
        return this.looters.contains(gamePlayer.getUniqueId());
    }

    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        if (event.getBlock().getType().equals(Material.CHEST)) {
            this.placedChestsLocations.add(event.getBlock().getLocation());
        }
    }

    @EventHandler
    public void onPlayerOpenChest(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!event.getClickedBlock().getType().equals(Material.CHEST)) return;
        if (this.placedChestsLocations.contains(event.getClickedBlock().getLocation())) return;

        this.looters.add(event.getPlayer().getUniqueId());
    }
}
