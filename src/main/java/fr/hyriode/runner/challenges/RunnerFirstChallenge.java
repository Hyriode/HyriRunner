package fr.hyriode.runner.challenges;

import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.challenges.HyriRunnerChallengeModel;
import fr.hyriode.runner.game.RunnerGamePlayer;
import org.bukkit.Material;

public class RunnerFirstChallenge extends RunnerChallenge {

    public RunnerFirstChallenge(HyriRunner plugin) {
        super(
                HyriRunnerChallengeModel.FIRST,
                "challenge.first.name",
                new String[] {"challenge.first.lore.1", "challenge.first.lore.2"},
                Material.CHAINMAIL_BOOTS,
                1
        );
        challengesMap.put(RunnerFirstChallenge.class, this);
    }

    @Override
    public boolean getCondition(RunnerGamePlayer player) {
        return player.getPosition() == 1;
    }

    @Override
    public void getReward(RunnerGamePlayer player) {
        sendSuccessMessage(player);
    }
}
