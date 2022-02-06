package fr.hyriode.runner.challenges;

import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.challenges.HyriRunnerChallengeModel;
import fr.hyriode.runner.game.HyriRunnerGamePlayer;
import org.bukkit.Material;

public class HyriRunnerWarriorChallenge extends HyriRunnerChallenge {


    public HyriRunnerWarriorChallenge(HyriRunner plugin) {
        super(plugin,
                HyriRunnerChallengeModel.WARRIOR,
                "challenge.warrior.name",
                new String[] {"challenge.warrior.lore.1", "challenge.warrior.lore.2"},
                2,
                Material.IRON_AXE);
        challengesMap.put(HyriRunnerWarriorChallenge.class, this);
    }

    @Override
    public boolean getCondition(HyriRunnerGamePlayer player) {
        if(getPlugin().getGame().getWinner() != null) {
            return player.isWarrior() && getPlugin().getGame().getWinner().contains(player);
        } else return false;
    }

    @Override
    public void getReward(HyriRunnerGamePlayer player) {
        player.sendMessage("Challenge warrior réussi lol");
    }
}
