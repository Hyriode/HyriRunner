package fr.hyriode.runner.challenge.model;

import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.RunnerChallengeModel;
import fr.hyriode.runner.challenge.RunnerChallenge;
import fr.hyriode.runner.challenge.RunnerChallengeDifficulty;
import fr.hyriode.runner.game.RunnerGamePlayer;
import org.bukkit.Material;

import java.util.List;

public class RunnerLastChallenge extends RunnerChallenge {

    private final HyriRunner plugin;

    public RunnerLastChallenge(HyriRunner plugin) {
        super(RunnerChallengeModel.LAST, "last", Material.MINECART, RunnerChallengeDifficulty.EASY);
        this.plugin = plugin;
    }

    @Override
    public boolean isValid(RunnerGamePlayer gamePlayer) {
        final List<RunnerGamePlayer> arrivedPlayers = this.plugin.getGame().getArrivedPlayers();

        return arrivedPlayers.size() > 0 && arrivedPlayers.get(arrivedPlayers.size() - 1).equals(gamePlayer);
    }

}
