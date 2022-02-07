package fr.hyriode.runner.challenges;

import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.challenges.HyriRunnerChallengeModel;
import fr.hyriode.runner.game.HyriRunnerGamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;

public class HyriRunnerWarriorChallenge extends HyriRunnerChallenge {

    private final HyriRunner plugin;

    public HyriRunnerWarriorChallenge(HyriRunner pl) {
        super(HyriRunnerChallengeModel.WARRIOR,
                "challenge.warrior.name",
                new String[] {"challenge.warrior.lore.1", "challenge.warrior.lore.2"},
                2,
                Material.IRON_AXE);
        challengesMap.put(HyriRunnerWarriorChallenge.class, this);
        this.plugin = pl;
    }

    @Override
    public boolean getCondition(HyriRunnerGamePlayer player) {
        if(plugin.getGame().getWinner() != null) {
            boolean winner = plugin.getGame().getWinner().contains(player);
            boolean warrior = player.isWarrior();
            return warrior && winner;
        } else return false;
    }

    @Override
    public void getReward(HyriRunnerGamePlayer player) {
        player.sendMessage("Challenge warrior réussi lol");
    }
}
