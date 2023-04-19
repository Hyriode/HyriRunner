package fr.hyriode.runner.api;

import fr.hyriode.api.mongodb.MongoDocument;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.model.IHyriPlayerData;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RunnerData implements IHyriPlayerData {

    private RunnerChallengeModel lastChallenge;

    private final Set<RunnerChallengeModel> completedChallenges = new HashSet<>();
    private final Set<RunnerChallengeModel> favoritesChallenges = new HashSet<>();

    public RunnerChallengeModel getLastChallenge() {
        return this.lastChallenge;
    }

    public void setLastChallenge(RunnerChallengeModel lastChallenge) {
        this.lastChallenge = lastChallenge;
    }

    public Set<RunnerChallengeModel> getCompletedChallenges() {
        return this.completedChallenges;
    }

    public void addCompletedChallenge(RunnerChallengeModel challenge) {
        this.completedChallenges.add(challenge);
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
        account.getData().add("therunner", this);
        account.update();
    }

    public static RunnerData get(UUID playerId) {
        RunnerData data = IHyriPlayer.get(playerId).getData().read("therunner", new RunnerData());

        if (data == null) {
            data = new RunnerData();
        }
        return data;
    }

    @Override
    public void save(MongoDocument document) {
        document.append("lastChallenge", this.lastChallenge == null ? null : this.lastChallenge.name());
        document.appendEnums("completedChallenges", this.completedChallenges);
        document.appendEnums("favoritesChallenges", this.favoritesChallenges);
    }

    @Override
    public void load(MongoDocument document) {
        final String lastChallengeStr = document.getString("lastChallenge");

        this.lastChallenge = lastChallengeStr == null ? null : RunnerChallengeModel.valueOf(lastChallengeStr);
        this.completedChallenges.addAll(document.getEnums("completedChallenges", RunnerChallengeModel.class));
        this.favoritesChallenges.addAll(document.getEnums("favoritesChallenges", RunnerChallengeModel.class));
    }

}
