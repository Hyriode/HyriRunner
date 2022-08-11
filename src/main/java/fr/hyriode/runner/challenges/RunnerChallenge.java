package fr.hyriode.runner.challenges;


import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.challenges.HyriRunnerChallengeModel;
import fr.hyriode.runner.game.RunnerGamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public abstract class RunnerChallenge {

    protected static final Map<Class<? extends RunnerChallenge>, RunnerChallenge> challengesMap = new HashMap<>();

    private final HyriRunnerChallengeModel model;

    private final String id;
    private final RunnerChallengeDifficulty difficulty;
    private final Material icon;

    private final HyriLanguageMessage name;
    private final HyriLanguageMessage lore;

    public RunnerChallenge(HyriRunnerChallengeModel model, String id, Material icon, RunnerChallengeDifficulty difficulty) {
        this.model = model;
        this.id = id;
        this.icon = icon;
        this.difficulty = difficulty;

        this.name = new HyriLanguageMessage("challenge." + id + ".name");
        this.lore = new HyriLanguageMessage("challenge." + id + ".lore");
    }

    public abstract boolean getCondition(RunnerGamePlayer player);

    public abstract void getReward(RunnerGamePlayer player);

    public static void registerChallenges(HyriRunner pl) {
        HyriRunner.log("Registering challenges...");

        /*  Add challenges here  */
        new RunnerFirstChallenge(pl);
        new RunnerWarriorChallenge(pl);
        new RunnerSerialKillerChallenge(pl);
        new RunnerNoBlockPlaced(pl);
        new RunnerArrivedChallenge(pl);
        new RunnerLastChallenge(pl);
        new RunnerSurvivorChallenge(pl);

        if (!challengesMap.isEmpty()) {
            challengesMap.values().forEach(challenge -> HyriRunner.log("Registered challenge:" + challenge.getModel().name()));

            HyriRunner.log("Registered " + challengesMap.size() + " challenges!");
        }
    }

    public static List<RunnerChallenge> getChallenges() {
        return new ArrayList<>(challengesMap.values());
    }
    public static List<RunnerChallenge> getChallenges(RunnerChallengeDifficulty difficulty) {
        return getChallenges().stream().filter(challenge -> challenge.difficulty.equals(difficulty)).collect(Collectors.toList());
    }

    public static Optional<RunnerChallenge> getWithModel(HyriRunnerChallengeModel model) {
        return challengesMap.values().stream().filter(challenge -> challenge.model.equals(model)).findFirst();
    }
    public static Optional<RunnerChallenge> getWithId(String id) {
        return challengesMap.values().stream().filter(challenge -> challenge.id.equalsIgnoreCase(id)).findFirst();
    }

    public String getName(final Player player) {
        return this.name.getValue(player);
    }
    public List<String> getLore(final Player player) {
        final String str = this.lore.getValue(player);
        final String[] splitLore = str.split("\n");

        return new ArrayList<>(Arrays.asList(splitLore));
    }

    public HyriRunnerChallengeModel getModel() {
        return this.model;
    }
    public String getId() {
        return this.id;
    }
    public Material getIcon() {
        return this.icon;
    }
    public RunnerChallengeDifficulty getDifficulty() {
        return this.difficulty;
    }
}
