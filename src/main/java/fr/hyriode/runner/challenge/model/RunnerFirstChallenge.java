package fr.hyriode.runner.challenge.model;

import fr.hyriode.runner.api.RunnerChallengeModel;
import fr.hyriode.runner.challenge.RunnerChallenge;
import fr.hyriode.runner.challenge.RunnerChallengeDifficulty;
import fr.hyriode.runner.game.RunnerGamePlayer;
import org.bukkit.Material;

public class RunnerFirstChallenge extends RunnerChallenge {

    public RunnerFirstChallenge() {
        super(RunnerChallengeModel.FIRST, "first", Material.CHAINMAIL_BOOTS, RunnerChallengeDifficulty.NORMAL);
    }

    @Override
    public boolean isValid(RunnerGamePlayer gamePlayer) {
        return gamePlayer.getPosition() == 1;
    }

}
