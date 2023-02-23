package fr.hyriode.runner.challenge.model;

import fr.hyriode.runner.api.RunnerChallengeModel;
import fr.hyriode.runner.challenge.RunnerChallenge;
import fr.hyriode.runner.challenge.RunnerChallengeDifficulty;
import fr.hyriode.runner.game.RunnerGamePlayer;
import org.bukkit.Material;

public class RunnerArrivedChallenge extends RunnerChallenge {

    public RunnerArrivedChallenge() {
        super(RunnerChallengeModel.FAST_ARRIVED, "fast-arrived", Material.WATCH, RunnerChallengeDifficulty.MEDIUM);
    }

    @Override
    public boolean isValid(RunnerGamePlayer gamePlayer) {
        return gamePlayer.isArrived() && gamePlayer.getArrivedTime() <= 3 * 60 + 10;
    }

}
