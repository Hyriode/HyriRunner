package fr.hyriode.runner.challenge.model;

import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.RunnerChallengeModel;
import fr.hyriode.runner.challenge.RunnerChallenge;
import fr.hyriode.runner.challenge.RunnerChallengeDifficulty;
import fr.hyriode.runner.game.RunnerGamePlayer;
import fr.hyriode.runner.game.phase.RunnerPhase;
import fr.hyriode.runner.game.phase.RunnerPhaseTriggeredEvent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RunnerLavaChallenge extends RunnerChallenge {

    private List<UUID> isValid;
    private HyriRunner plugin;

    public RunnerLavaChallenge(HyriRunner plugin) {
        super(RunnerChallengeModel.LAVA, "lava", Material.LAVA_BUCKET, RunnerChallengeDifficulty.EASY);
        this.plugin = plugin;
        this.isValid = new ArrayList<>();
    }

    @HyriEventHandler
    public void onGamePhaseChange(RunnerPhaseTriggeredEvent event) {
        if (event.getPhase() != RunnerPhase.BORDER_END) {
            return;
        }
        for (RunnerGamePlayer gamePlayer : this.plugin.getGame().getPlayers()) {
            for (ItemStack content : gamePlayer.getPlayer().getInventory().getContents()) {
                if (content == null) {
                    continue;
                }
                if (content.getType() != Material.LAVA_BUCKET) {
                    continue;
                }
                this.isValid.add(gamePlayer.getPlayer().getUniqueId());
                break;
            }
        }
    }

    @Override
    public boolean isValid(RunnerGamePlayer gamePlayer) {
        return isValid.contains(gamePlayer.getPlayer().getUniqueId());
    }
}
