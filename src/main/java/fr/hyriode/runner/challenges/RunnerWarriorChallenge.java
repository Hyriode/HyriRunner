package fr.hyriode.runner.challenges;

import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.challenges.HyriRunnerChallengeModel;
import fr.hyriode.runner.game.RunnerGamePlayer;
import org.bukkit.Material;

public class RunnerWarriorChallenge extends RunnerChallenge {

    private final HyriRunner plugin;

    public RunnerWarriorChallenge(HyriRunner pl) {
        super(
                HyriRunnerChallengeModel.WARRIOR,
                "challenge.warrior.name",
                new String[] {"challenge.warrior.lore.1", "challenge.warrior.lore.2"},
                Material.IRON_AXE,
                2
        );
        challengesMap.put(RunnerWarriorChallenge.class, this);
        this.plugin = pl;
    }

    @Override
    public boolean getCondition(RunnerGamePlayer player) {
        if(plugin.getGame().getWinner() != null) {
            boolean winner = plugin.getGame().getWinner().contains(player);
            boolean warrior = player.isWarrior();
            return warrior && winner;
        } else return false;
    }

    @Override
    public void getReward(RunnerGamePlayer player) {
        sendSuccessMessage(player);
    }
}
