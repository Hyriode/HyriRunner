package fr.hyriode.runner.api.player;

import fr.hyriode.api.player.HyriPlayerData;
import fr.hyriode.runner.api.challenges.HyriRunnerChallengeModel;
import fr.hyriode.runner.api.statistics.HyriRunnerStatistics;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class HyriRunnerPlayer extends HyriPlayerData {

    private final UUID uniqueId;
    private HyriRunnerStatistics statistics;
    private final Set<HyriRunnerChallengeModel> completedChallenges;
    private HyriRunnerChallengeModel lastSelectedChallenge;

    public HyriRunnerPlayer(UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.statistics = new HyriRunnerStatistics();
        this.completedChallenges = new HashSet<>();
        this.lastSelectedChallenge = null;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public HyriRunnerStatistics getStatistics() {
        return this.statistics;
    }

    public void setStatistics(HyriRunnerStatistics statistics) {
        this.statistics = statistics;
    }

    public Set<HyriRunnerChallengeModel> getCompletedChallenges() {
        return completedChallenges;
    }

    public HyriRunnerChallengeModel getLastSelectedChallenge() {
        return lastSelectedChallenge;
    }

    public void setLastSelectedChallenge(HyriRunnerChallengeModel lastSelectedChallenge) {
        this.lastSelectedChallenge = lastSelectedChallenge;
    }
}
