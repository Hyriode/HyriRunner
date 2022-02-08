package fr.hyriode.runner.challenges;

import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.challenges.HyriRunnerChallengeModel;
import fr.hyriode.runner.game.HyriRunnerGamePlayer;
import org.bukkit.Material;

public class HyriRunnerArrivedChallenge extends HyriRunnerChallenge {

    public HyriRunnerArrivedChallenge(HyriRunner pl) {
        super(
                HyriRunnerChallengeModel.FAST_ARRIVED,
                "challenge.fast-arrived.name",
                new String[]{"challenge.fast-arrived.lore.1", "challenge.fast-arrived.lore.2"},
                Material.WATCH
        );
        challengesMap.put(HyriRunnerArrivedChallenge.class, this);
    }

    @Override
    public boolean getCondition(HyriRunnerGamePlayer player) {
        if (player.isArrived()) {
            return player.getArrivedTime() <= 190;
        } else {
            return false;
        }
    }

    @Override
    public void getReward(HyriRunnerGamePlayer player) {
        sendSuccessMessage(player);
    }
}
