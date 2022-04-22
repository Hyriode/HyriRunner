package fr.hyriode.runner.challenges;

import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.challenges.HyriRunnerChallengeModel;
import fr.hyriode.runner.game.RunnerGamePlayer;
import org.bukkit.Material;

public class RunnerLastChallenge extends RunnerChallenge {

    private final HyriRunner plugin;

    public RunnerLastChallenge(HyriRunner pl) {
        super(
                HyriRunnerChallengeModel.LAST,
                "challenge.last.name",
                new String[] {"challenge.last.lore.1", "challenge.last.lore.2"},
                Material.MINECART,
                3
        );
        challengesMap.put(RunnerLastChallenge.class, this);
        this.plugin = pl;
    }

    @Override
    public boolean getCondition(RunnerGamePlayer player) {
        return plugin.getGame().getArrivedPlayers().get(plugin.getGame().getArrivedPlayers().size() - 1).equals(player);
    }

    @Override
    public void getReward(RunnerGamePlayer player) {
        sendSuccessMessage(player);
    }
}
