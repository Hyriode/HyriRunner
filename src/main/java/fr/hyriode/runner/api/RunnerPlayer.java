package fr.hyriode.runner.api;

import fr.hyriode.api.player.HyriPlayerData;
import fr.hyriode.api.player.IHyriPlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RunnerPlayer extends HyriPlayerData {

    private final Set<RunnerChallengeModel> completedChallenges;
    private RunnerChallengeModel lastSelectedChallenge;
    private final Set<RunnerChallengeModel> favoritesChallenges;

    public RunnerPlayer() {
        this.completedChallenges = new HashSet<>();
        this.favoritesChallenges = new HashSet<>();
    }

    public Set<RunnerChallengeModel> getCompletedChallenges() {
        return this.completedChallenges;
    }

    public void addCompletedChallenge(RunnerChallengeModel challenge) {
        this.completedChallenges.add(challenge);
    }

    public RunnerChallengeModel getLastSelectedChallenge() {
        return this.lastSelectedChallenge;
    }

    public void setLastSelectedChallenge(RunnerChallengeModel lastSelectedChallenge) {
        this.lastSelectedChallenge = lastSelectedChallenge;
    }

    public Set<RunnerChallengeModel> getFavoritesChallenges() {
        return this.favoritesChallenges;
    }

    public boolean addFavoriteChallenge(RunnerChallengeModel challenge) {
        return this.favoritesChallenges.add(challenge);
    }

    public void removeFavoriteChallenge(RunnerChallengeModel challenge) {
        this.favoritesChallenges.remove(challenge);
    }

    public void update(IHyriPlayer account) {
        account.addData("therunner", this);
        account.update();
    }

    public static RunnerPlayer get(UUID playerId) {
        final IHyriPlayer account = IHyriPlayer.get(playerId);
        final RunnerPlayer player = account.getData("therunner", RunnerPlayer.class);

        return player == null ? new RunnerPlayer() : player;
    }

}
