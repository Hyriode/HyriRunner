package fr.hyriode.runner.challenges;

import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.challenges.HyriRunnerChallengeModel;
import fr.hyriode.runner.game.RunnerGamePlayer;
import org.bukkit.Material;

public class RunnerArrivedChallenge extends RunnerChallenge {

    public RunnerArrivedChallenge(HyriRunner pl) {
        super(
                HyriRunnerChallengeModel.FAST_ARRIVED,
                "challenge.fast-arrived.name",
                new String[]{"challenge.fast-arrived.lore.1", "challenge.fast-arrived.lore.2"},
                Material.WATCH,
                3
        );
        challengesMap.put(RunnerArrivedChallenge.class, this);
    }

    @Override
    public boolean getCondition(RunnerGamePlayer player) {
        if (player.isArrived()) {
            return player.getArrivedTime() <= 190;
        } else {
            return false;
        }
    }

    @Override
    public void getReward(RunnerGamePlayer player) {
        sendSuccessMessage(player);
    }
}
