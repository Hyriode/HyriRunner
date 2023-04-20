package fr.hyriode.runner.api;

import fr.hyriode.api.mongodb.MongoDocument;
import fr.hyriode.api.mongodb.MongoSerializable;
import fr.hyriode.api.mongodb.MongoSerializer;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.model.IHyriStatistics;
import fr.hyriode.runner.game.RunnerGameType;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RunnerStatistics implements IHyriStatistics {

    private final Map<RunnerGameType, Data> dataMap = new HashMap<>();

    public Map<RunnerGameType, Data> getData() {
        return this.dataMap;
    }

    @Override
    public void save(MongoDocument document) {
        for (Map.Entry<RunnerGameType, Data> entry : this.dataMap.entrySet()) {
            document.append(entry.getKey().name(), MongoSerializer.serialize(entry.getValue()));
        }
    }

    @Override
    public void load(MongoDocument document) {
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            final MongoDocument dataDocument = MongoDocument.of((Document) entry.getValue());
            final Data data = new Data();

            data.load(dataDocument);

            this.dataMap.put(RunnerGameType.valueOf(entry.getKey()), data);
        }
    }

    public Data getData(RunnerGameType gameType) {
        return this.dataMap.merge(gameType, new Data(), (oldValue, newValue) -> oldValue);
    }

    public void update(IHyriPlayer account) {
        account.getStatistics().add("therunner", this);
        account.update();
    }

    public static RunnerStatistics get(IHyriPlayer account) {
        RunnerStatistics statistics = account.getStatistics().read("therunner", new RunnerStatistics());

        if (statistics == null) {
            statistics = new RunnerStatistics();
            statistics.update(account);
        }
        return statistics;
    }

    public static RunnerStatistics get(UUID playerId) {
        return get(IHyriPlayer.get(playerId));
    }

    public static class Data implements MongoSerializable {

        private long kills;
        private long deaths;

        private long bestRun;

        private long successfulRuns;
        private long victories;
        private long gamesPlayed;

        @Override
        public void save(MongoDocument document) {
            document.append("kills", this.kills);
            document.append("deaths", this.deaths);
            document.append("bestRun", this.bestRun);
            document.append("successfulRuns", this.successfulRuns);
            document.append("victories", this.victories);
            document.append("gamesPlayed", this.gamesPlayed);
        }

        @Override
        public void load(MongoDocument document) {
            this.kills = document.getLong("kills");
            this.deaths = document.getLong("deaths");
            this.bestRun = document.containsKey("bestRun") ? document.getLong("bestRun") : -1;
            this.successfulRuns = document.getLong("successfulRuns");
            this.victories = document.getLong("victories");
            this.gamesPlayed = document.getLong("gamesPlayed");
        }

        public long getKills() {
            return this.kills;
        }

        public void addKills(long kills) {
            this.kills += kills;
        }

        public long getDeaths() {
            return this.deaths;
        }

        public void addDeaths(long deaths) {
            this.deaths += deaths;
        }

        public long getBestRun() {
            return this.bestRun;
        }

        public void setBestRun(long bestRun) {
            this.bestRun = bestRun;
        }

        public long getVictories() {
            return this.victories;
        }

        public void addVictories(long victories) {
            this.victories += victories;
        }

        public long getGamesPlayed() {
            return this.gamesPlayed;
        }

        public void addGamesPlayed(int gamesPlayed) {
            this.gamesPlayed += gamesPlayed;
        }

        public long getSuccessfulRuns() {
            return successfulRuns;
        }

        public void addSuccessfulRun(int successfulRun) {
            this.successfulRuns += successfulRun;
        }

    }

}
