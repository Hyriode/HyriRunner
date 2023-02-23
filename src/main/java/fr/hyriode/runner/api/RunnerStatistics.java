package fr.hyriode.runner.api;

import com.mongodb.Mongo;
import fr.hyriode.api.mongodb.MongoDocument;
import fr.hyriode.api.mongodb.MongoSerializable;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.model.IHyriPlayerData;
import fr.hyriode.api.player.model.IHyriStatistics;
import fr.hyriode.runner.game.RunnerGameType;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RunnerStatistics implements IHyriStatistics {

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
        account.getStatistics().add("therunner", this);
        account.update();
    }

    public static RunnerStatistics get(IHyriPlayer account) {
        RunnerStatistics statistics = account.getStatistics().get("therunner");

        if (statistics == null) {
            statistics = new RunnerStatistics();
            statistics.update(account);
        }

        return statistics;
    }

    public static RunnerStatistics get(UUID playerId) {
        return get(IHyriPlayer.get(playerId));
    }

    @Override
    public void save(MongoDocument document) {
        for (Map.Entry<RunnerGameType, Data> entry : this.data.entrySet()) {
            final Document dataDocument = new Document();

            entry.getValue().save(MongoDocument.of(dataDocument));

            document.append(entry.getKey().name(), dataDocument);
        }
    }

    @Override
    public void load(MongoDocument document) {
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            final Data data = new Data();

            data.load(MongoDocument.of((Document) entry.getValue()));

            this.data.put(RunnerGameType.valueOf(entry.getKey()), data);
        }
    }

    public static class Data implements MongoSerializable {

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

        @Override
        public void save(MongoDocument document) {
            document.append("kills", this.kills);
            document.append("final_kills", this.finalKills);
            document.append("deaths", this.deaths);

            document.append("successful_runs", this.successfulRuns);
            document.append("victories", this.victories);
            document.append("games_played", this.gamesPlayed);
            document.append("played_time", this.playedTime);
        }

        @Override
        public void load(MongoDocument document) {
            this.kills = document.getLong("kills");
            this.finalKills = document.getLong("final_kills");
            this.deaths = document.getLong("deaths");

            this.successfulRuns = document.getLong("successful_runs");
            this.victories = document.getLong("victories");
            this.gamesPlayed = document.getLong("games_played");
            this.playedTime = document.getLong("played_time");
        }

    }

}
