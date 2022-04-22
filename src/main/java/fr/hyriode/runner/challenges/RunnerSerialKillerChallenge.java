package fr.hyriode.runner.challenges;

import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.challenges.HyriRunnerChallengeModel;
import fr.hyriode.runner.game.RunnerGamePlayer;
import org.bukkit.Material;

public class RunnerSerialKillerChallenge extends RunnerChallenge {

    private final HyriRunner plugin;

    public RunnerSerialKillerChallenge(HyriRunner pl) {
        super(
                HyriRunnerChallengeModel.SERIAL_KILLER,
                "challenge.serial-killer.name",
                new String[] {"challenge.serial-killer.lore.1", "challenge.serial-killer.lore.2"},
                Material.DIAMOND_SWORD,
                3
        );
        challengesMap.put(RunnerSerialKillerChallenge.class, this);
        this.plugin = pl;
    }

    @Override
    public boolean getCondition(RunnerGamePlayer player) {
        if(plugin.getGame().isPvp()) {
            if(plugin.getGame().getPlayersPvpPhaseRemaining() == player.getKills()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void getReward(RunnerGamePlayer player) {
        sendSuccessMessage(player);
    }
}
