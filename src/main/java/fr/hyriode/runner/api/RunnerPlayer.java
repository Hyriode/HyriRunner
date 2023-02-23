package fr.hyriode.runner.api;

import fr.hyriode.api.mongodb.MongoDocument;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.model.IHyriPlayerData;
import fr.hyriode.runner.challenge.RunnerChallenge;
import org.bukkit.Bukkit;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class RunnerPlayer implements IHyriPlayerData {

    private Set<RunnerChallengeModel> completedChallenges;
    private RunnerChallengeModel lastSelectedChallenge;
    private Set<RunnerChallengeModel> favoritesChallenges;

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
        account.getData().add("therunner", this);
        account.update();
    }

    public static RunnerPlayer get(UUID playerId) {
        final IHyriPlayer account = IHyriPlayer.get(playerId);
        final RunnerPlayer player = account.getData().read("therunner", new RunnerPlayer());

        return player == null ? new RunnerPlayer() : player;
    }

    @Override
    public void save(MongoDocument document) {
        document.append("completed_challenges", this.completedChallenges.stream().map(RunnerChallengeModel::name).collect(Collectors.toList()));
        document.append("last_selected_challenge", this.lastSelectedChallenge == null ? null : this.lastSelectedChallenge.name());
        document.append("favorites_challenges", this.favoritesChallenges.stream().map(RunnerChallengeModel::name).collect(Collectors.toList()));
    }

    @Override
    public void load(MongoDocument document) {
        this.completedChallenges = document.getList("completed_challenges", String.class).stream()
                .map(RunnerChallengeModel::valueOf).collect(Collectors.toSet());

        this.lastSelectedChallenge =
                document.getString("last_selected_challenge") == null ?
                        null :
                        RunnerChallengeModel.valueOf(document.getString("last_selected_challenge"));

        this.favoritesChallenges = document.getList("favorites_challenges", String.class).stream()
                .map(RunnerChallengeModel::valueOf).collect(Collectors.toSet());
    }

}
