package fr.hyriode.runner.challenges;

import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.challenges.HyriRunnerChallengeModel;
import fr.hyriode.runner.game.HyriRunnerGamePlayer;
import org.bukkit.Material;

public class HyriRunnerSerialKillerChallenge extends HyriRunnerChallenge {

    private final HyriRunner plugin;

    public HyriRunnerSerialKillerChallenge(HyriRunner pl) {
        super(
                HyriRunnerChallengeModel.SERIAL_KILLER,
                "challenge.serial-killer.name",
                new String[] {"challenge.serial-killer.lore.1", "challenge.serial-killer.lore.2"},
                Material.DIAMOND_SWORD
        );
        challengesMap.put(HyriRunnerSerialKillerChallenge.class, this);
        this.plugin = pl;
    }

    @Override
    public boolean getCondition(HyriRunnerGamePlayer player) {
        if(plugin.getGame().isPvp()) {
            if(plugin.getGame().getPlayersPvpPhaseRemaining() == player.getKills()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void getReward(HyriRunnerGamePlayer player) {
        sendSuccessMessage(player);
    }
}
