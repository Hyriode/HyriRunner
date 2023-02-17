package fr.hyriode.runner.api;

import fr.hyriode.api.player.HyriPlayerData;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.runner.game.RunnerGameType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RunnerStatistics extends HyriPlayerData {

    private final Map<RunnerGameType, Data> data;

    public RunnerStatistics() {
        this.data = new HashMap<>();
    }

    public Map<RunnerGameType, Data> getData() {
        return this.data;
    }

    public Data getData(RunnerGameType gameType) {
        Data data = this.data.get(gameType);

        if (data == null) {
            data = new Data();
            this.data.put(gameType, data);
        }

        return data;
    }

    public void update(IHyriPlayer account) {
        account.addStatistics("therunner", this);
        account.update();
    }

    public static RunnerStatistics get(IHyriPlayer account) {
        RunnerStatistics statistics = account.getStatistics("therunner", RunnerStatistics.class);

        if (statistics == null) {
            statistics = new RunnerStatistics();
            statistics.update(account);
        }

        return statistics;
    }

    public static RunnerStatistics get(UUID playerId) {
        return get(IHyriPlayer.get(playerId));
    }

    public static class Data {

        private long kills;
        private long finalKills;
        private long deaths;

        private long successfulRuns;
        private long victories;
        private long gamesPlayed;
        private long playedTime;

        public long getKills() {
            return this.kills;
        }

        public void setKills(long kills) {
            this.kills = kills;
        }

        public void addKills(long kills) {
            this.kills += kills;
        }

        public void removeKills(int kills) {
            this.kills -= kills;
        }

        public long getFinalKills() {
            return this.finalKills;
        }

        public void setFinalKills(long finalKills) {
            this.finalKills = finalKills;
        }

        public void addFinalKills(long finalKills) {
            this.finalKills += finalKills;
        }

        public void removeFinalKills(long finalKills) {
            this.finalKills -= finalKills;
        }

        public long getDeaths() {
            return this.deaths;
        }

        public void setDeaths(long deaths) {
            this.deaths = deaths;
        }

        public void addDeaths(long deaths) {
            this.deaths += deaths;
        }

        public void removeDeaths(long deaths) {
            this.deaths -= deaths;
        }

        public long getVictories() {
            return this.victories;
        }

        public void setVictories(long victories) {
            this.victories = victories;
        }

        public void addVictories(long victories) {
            this.victories += victories;
        }

        public void removeVictories(long victories) {
            this.victories -= victories;
        }

        public long getDefeats() {
            return this.gamesPlayed - this.victories;
        }

        public long getGamesPlayed() {
            return this.gamesPlayed;
        }

        public void setGamesPlayed(long gamesPlayed) {
            this.gamesPlayed = gamesPlayed;
        }

        public void addGamesPlayed(int gamesPlayed) {
            this.gamesPlayed += gamesPlayed;
        }

        public void removeGamesPlayed(int gamesPlayed) {
            this.gamesPlayed -= gamesPlayed;
        }

        public long getPlayedTime() {
            return this.playedTime;
        }

        public void setPlayedTime(long playedTime) {
            this.playedTime = playedTime;
        }

        public long getSuccessfulRuns() {
            return successfulRuns;
        }

        public void setSuccessfulRuns(long successfulRuns) {
            this.successfulRuns = successfulRuns;
        }

        public void addSuccessfulRun(int successfulRun) {
            this.successfulRuns += successfulRun;
        }

        public void removeSuccessfulRun(int successfulRun) {
            this.successfulRuns -= successfulRun;
        }

    }

}
