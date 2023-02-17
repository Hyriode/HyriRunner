package fr.hyriode.runner.challenge.model;

import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.RunnerChallengeModel;
import fr.hyriode.runner.challenge.RunnerChallenge;
import fr.hyriode.runner.challenge.RunnerChallengeDifficulty;
import fr.hyriode.runner.game.RunnerGamePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Project: Hyriode
 * Created by Akkashi
 * on 30/06/2022 at 11:41
 */
public class RunnerSurvivorChallenge extends RunnerChallenge {

    private final Map<UUID, Boolean> damagesTaken = new HashMap<>();

    private final HyriRunner plugin;

    public RunnerSurvivorChallenge(HyriRunner plugin) {
        super(RunnerChallengeModel.SURVIVOR, "survivor", Material.REDSTONE, RunnerChallengeDifficulty.HARD);
        this.plugin = plugin;
    }

    @Override
    public boolean isValid(RunnerGamePlayer gamePlayer) {
        return gamePlayer.isArrived() && !this.damagesTaken.containsKey(gamePlayer.getUniqueId());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        final Entity entity = event.getEntity();

        if (entity instanceof Player) {
            final Player player = (Player) entity;
            final RunnerGamePlayer gamePlayer = this.plugin.getGame().getPlayer(player);

            if (gamePlayer == null || gamePlayer.isArrived()) {
                return;
            }

            this.damagesTaken.putIfAbsent(player.getUniqueId(), true);
        }
    }

}
