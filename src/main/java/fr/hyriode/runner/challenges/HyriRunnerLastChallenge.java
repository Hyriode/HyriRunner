package fr.hyriode.runner.challenges;

import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.challenges.HyriRunnerChallengeModel;
import fr.hyriode.runner.game.HyriRunnerGamePlayer;
import org.bukkit.Material;

public class HyriRunnerLastChallenge extends HyriRunnerChallenge {

    private final HyriRunner plugin;

    public HyriRunnerLastChallenge(HyriRunner pl) {
        super(
                HyriRunnerChallengeModel.LAST,
                "challenge.last.name",
                new String[] {"challenge.last.lore.1", "challenge.last.lore.2"},
                Material.MINECART
        );
        challengesMap.put(HyriRunnerLastChallenge.class, this);
        this.plugin = pl;
    }

    @Override
    public boolean getCondition(HyriRunnerGamePlayer player) {
        return plugin.getGame().getArrivedPlayers().get(plugin.getGame().getArrivedPlayers().size() - 1).equals(player);
    }

    @Override
    public void getReward(HyriRunnerGamePlayer player) {
        sendSuccessMessage(player);
    }
}
