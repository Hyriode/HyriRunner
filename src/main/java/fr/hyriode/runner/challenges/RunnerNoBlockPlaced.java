package fr.hyriode.runner.challenges;

import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.challenges.HyriRunnerChallengeModel;
import fr.hyriode.runner.game.RunnerGamePlayer;
import org.bukkit.Material;

public class RunnerNoBlockPlaced extends RunnerChallenge {
    public RunnerNoBlockPlaced(HyriRunner pl) {
        super(
                HyriRunnerChallengeModel.NO_BLOCK_PLACED,
                "challenge.no-block-placed.name",
                new String[]{"challenge.no-block-placed.lore.1", "challenge.no-block-placed.lore.2"},
                Material.STONE,
                2
        );
        challengesMap.put(RunnerNoBlockPlaced.class, this);
    }

    @Override
    public boolean getCondition(RunnerGamePlayer player) {
        return player.isArrived() && player.getBlocksPlaced() <= 16;
    }

    @Override
    public void getReward(RunnerGamePlayer player) {
        sendSuccessMessage(player);
    }
}
