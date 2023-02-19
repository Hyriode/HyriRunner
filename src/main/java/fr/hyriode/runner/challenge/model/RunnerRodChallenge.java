package fr.hyriode.runner.challenge.model;

import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.RunnerChallengeModel;
import fr.hyriode.runner.challenge.RunnerChallenge;
import fr.hyriode.runner.challenge.RunnerChallengeDifficulty;
import fr.hyriode.runner.game.RunnerGamePlayer;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RunnerRodChallenge extends RunnerChallenge implements Listener {

    private final HyriRunner plugin;
    private final List<UUID> isValid;

    public RunnerRodChallenge(HyriRunner plugin) {
        super(RunnerChallengeModel.ROD, "rod", Material.FISHING_ROD, RunnerChallengeDifficulty.NORMAL);
        this.isValid = new ArrayList<>();
        this.plugin = plugin;

        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onPlayerCraft(CraftItemEvent event) {
        if (event.getRecipe().getResult().getType() == Material.FISHING_ROD) {
            this.isValid.add(event.getWhoClicked().getUniqueId());
        }
    }

    @Override
    public boolean isValid(RunnerGamePlayer gamePlayer) {
        return this.isValid.contains(gamePlayer.getUniqueId());
    }
}
