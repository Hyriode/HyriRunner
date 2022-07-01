package fr.hyriode.runner.challenges;

import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.challenges.HyriRunnerChallengeModel;
import fr.hyriode.runner.game.RunnerGamePlayer;
import org.bukkit.Material;

import java.util.List;
import java.util.UUID;

public class RunnerLastChallenge extends RunnerChallenge {

    private final HyriRunner plugin;

    public RunnerLastChallenge(HyriRunner pl) {
        super(
                HyriRunnerChallengeModel.LAST,
                "challenge.last.name",
                new String[] {"challenge.last.lore.1", "challenge.last.lore.2"},
                Material.MINECART,
                1
        );
        challengesMap.put(RunnerLastChallenge.class, this);
        this.plugin = pl;
    }

    @Override
    public boolean getCondition(RunnerGamePlayer player) {
        final List<RunnerGamePlayer> arrivedPlayers = this.plugin.getGame().getArrivedPlayers();

        if (arrivedPlayers.size() > 0) {
            return arrivedPlayers.get(arrivedPlayers.size() - 1).equals(player);
        }
        return false;
    }

    @Override
    public void getReward(RunnerGamePlayer player) {
        sendSuccessMessage(player);
    }
}
