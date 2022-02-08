package fr.hyriode.runner.challenges;

import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.challenges.HyriRunnerChallengeModel;
import fr.hyriode.runner.game.HyriRunnerGamePlayer;
import org.bukkit.Material;

public class HyriRunnerFirstChallenge extends HyriRunnerChallenge {

    public HyriRunnerFirstChallenge(HyriRunner plugin) {
        super(
                HyriRunnerChallengeModel.FIRST,
                "challenge.first.name",
                new String[] {"challenge.first.lore.1", "challenge.first.lore.2"},
                Material.CHAINMAIL_BOOTS
        );
        challengesMap.put(HyriRunnerFirstChallenge.class, this);
    }

    @Override
    public boolean getCondition(HyriRunnerGamePlayer player) {
        return player.getPosition() == 1;
    }

    @Override
    public void getReward(HyriRunnerGamePlayer player) {
        sendSuccessMessage(player);
    }
}
