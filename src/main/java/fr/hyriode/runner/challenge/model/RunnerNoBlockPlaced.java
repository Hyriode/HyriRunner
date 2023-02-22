package fr.hyriode.runner.challenge.model;

import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.RunnerChallengeModel;
import fr.hyriode.runner.challenge.RunnerChallenge;
import fr.hyriode.runner.challenge.RunnerChallengeDifficulty;
import fr.hyriode.runner.game.RunnerGamePlayer;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RunnerNoBlockPlaced extends RunnerChallenge implements Listener {

    private final Map<UUID, Integer> blocksPlaced = new HashMap<>();

    private final HyriRunner plugin;

    public RunnerNoBlockPlaced(HyriRunner plugin) {
        super(RunnerChallengeModel.NO_BLOCK_PLACED, "no-block-placed", Material.STONE, RunnerChallengeDifficulty.EXTREME);
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @Override
    public boolean isValid(RunnerGamePlayer gamePlayer) {
        return gamePlayer.isArrived() && this.blocksPlaced.get(gamePlayer.getUniqueId()) <= 16;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        final UUID playerId = event.getPlayer().getUniqueId();
        final RunnerGamePlayer gamePlayer = this.plugin.getGame().getPlayer(playerId);

        if (gamePlayer == null || gamePlayer.isArrived()) {
            return;
        }

        final int current = this.blocksPlaced.getOrDefault(playerId, 0);

        this.blocksPlaced.put(playerId, current + 1);
    }

}
