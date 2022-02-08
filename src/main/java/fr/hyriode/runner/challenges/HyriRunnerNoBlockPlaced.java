package fr.hyriode.runner.challenges;

import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.challenges.HyriRunnerChallengeModel;
import fr.hyriode.runner.game.HyriRunnerGamePlayer;
import org.bukkit.Material;

public class HyriRunnerNoBlockPlaced extends HyriRunnerChallenge {
    public HyriRunnerNoBlockPlaced(HyriRunner pl) {
        super(
                HyriRunnerChallengeModel.NO_BLOCK_PLACED,
                "challenge.no-block-placed.name",
                new String[]{"challenge.no-block-placed.lore.1", "challenge.no-block-placed.lore.2"},
                Material.STONE
        );
        challengesMap.put(HyriRunnerNoBlockPlaced.class, this);
    }

    @Override
    public boolean getCondition(HyriRunnerGamePlayer player) {
        return player.isArrived() && player.getBlocksPlaced() <= 16;
    }

    @Override
    public void getReward(HyriRunnerGamePlayer player) {
        sendSuccessMessage(player);
    }
}
