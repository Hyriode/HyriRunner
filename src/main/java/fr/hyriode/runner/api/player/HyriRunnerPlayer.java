package fr.hyriode.runner.api.player;

import fr.hyriode.runner.api.statistics.HyriRunnerStatistics;

import java.util.UUID;

public class HyriRunnerPlayer {

    private final UUID uniqueId;
    private HyriRunnerStatistics statistics;

    public HyriRunnerPlayer(UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.statistics = new HyriRunnerStatistics();
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

}
