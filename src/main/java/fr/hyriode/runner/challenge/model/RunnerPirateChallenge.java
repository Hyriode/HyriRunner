package fr.hyriode.runner.challenge.model;

import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.RunnerChallengeModel;
import fr.hyriode.runner.challenge.RunnerChallenge;
import fr.hyriode.runner.challenge.RunnerChallengeDifficulty;
import fr.hyriode.runner.game.RunnerGamePlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RunnerPirateChallenge extends RunnerChallenge implements Listener {

    private final Map<UUID, Double> blocksTraveled = new HashMap<>();
    private final Map<UUID, Location> lastLocation = new HashMap<>();

    private final HyriRunner plugin;

    public RunnerPirateChallenge(HyriRunner plugin) {
        super(RunnerChallengeModel.PIRATE, "pirate", Material.BOAT, RunnerChallengeDifficulty.NORMAL);
        this.plugin = plugin;

        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @Override
    public boolean isValid(RunnerGamePlayer gamePlayer) {
        return this.blocksTraveled.get(gamePlayer.getUniqueId()) >= 40;
    }

    @EventHandler
    public void onPlayerEnterVehicle(VehicleEnterEvent event) {
        if (!(event.getVehicle() instanceof Boat)) {
            return;
        }

        this.lastLocation.put(event.getEntered().getUniqueId(), event.getEntered().getLocation());
    }

    @EventHandler
    public void onPlayerExitVehicle(VehicleExitEvent event) {
        if (!(event.getVehicle() instanceof Boat)) {
            return;
        }

        if (!this.lastLocation.containsKey(event.getExited().getUniqueId())) {
            return;
        }

        double current = 0;
        if (this.blocksTraveled.containsKey(event.getExited().getUniqueId())) {
            current = this.blocksTraveled.get(event.getExited().getUniqueId());
        }

        blocksTraveled.put(event.getExited().getUniqueId(), (current + this.lastLocation.get(event.getExited().getUniqueId()).distance(event.getExited().getLocation())));
    }
}
