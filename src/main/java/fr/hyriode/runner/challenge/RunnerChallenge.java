package fr.hyriode.runner.challenge;


import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.RunnerChallengeModel;
import fr.hyriode.runner.challenge.model.*;
import fr.hyriode.runner.game.RunnerGamePlayer;
import fr.hyriode.runner.util.RunnerMessage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public abstract class RunnerChallenge {

    private static final Map<Class<? extends RunnerChallenge>, RunnerChallenge> CHALLENGES = new HashMap<>();

    private final RunnerChallengeModel model;

    private final String id;
    private final RunnerChallengeDifficulty difficulty;
    private final Material icon;

    private final HyriLanguageMessage name;
    private final HyriLanguageMessage lore;

    public RunnerChallenge(RunnerChallengeModel model, String id, Material icon, RunnerChallengeDifficulty difficulty) {
        this.model = model;
        this.id = id;
        this.icon = icon;
        this.difficulty = difficulty;;
        this.name = HyriLanguageMessage.get("challenge." + id + ".name");
        this.lore = HyriLanguageMessage.get("challenge." + id + ".lore");

        HyriAPI.get().getEventBus().register(this);
    }

    public abstract boolean isValid(RunnerGamePlayer gamePlayer);

    public void rewardPlayer(RunnerGamePlayer gamePlayer) {
        final Player player = gamePlayer.getPlayer();

        gamePlayer.getData().addCompletedChallenge(this.model);

        player.sendMessage(RunnerMessage.CHALLENGE_COMPLETED.asString(player).replace("%challenge%", this.getName(player)));
    }

    public static void registerChallenges(HyriRunner plugin) {
        HyriRunner.log("Registering challenges...");

        /*  Add challenges here  */
        registerChallenge(new RunnerFirstChallenge());
        registerChallenge(new RunnerWarriorChallenge(plugin));
        registerChallenge(new RunnerSerialKillerChallenge(plugin));
        registerChallenge(new RunnerNoBlockPlaced(plugin));
        registerChallenge(new RunnerArrivedChallenge());
        registerChallenge(new RunnerLastChallenge(plugin));
        registerChallenge(new RunnerSurvivorChallenge(plugin));
        registerChallenge(new RunnerLavaChallenge(plugin));
        registerChallenge(new RunnerRodChallenge(plugin));

        if (!CHALLENGES.isEmpty()) {
            HyriRunner.log("Registered " + CHALLENGES.size() + " challenges!");
        }
    }

    public static void registerChallenge(RunnerChallenge challenge) {
        CHALLENGES.put(challenge.getClass(), challenge);

        HyriRunner.log("Registered '" + challenge.getModel().name() + "' challenge.");
    }

    public static List<RunnerChallenge> getChallenges() {
        return new ArrayList<>(CHALLENGES.values());
    }

    public static List<RunnerChallenge> getChallenges(RunnerChallengeDifficulty difficulty) {
        return getChallenges().stream().filter(challenge -> challenge.difficulty.equals(difficulty)).collect(Collectors.toList());
    }

    public static Optional<RunnerChallenge> getWithModel(RunnerChallengeModel model) {
        return CHALLENGES.values().stream().filter(challenge -> challenge.model.equals(model)).findFirst();
    }
    public static Optional<RunnerChallenge> getWithId(String id) {
        return CHALLENGES.values().stream().filter(challenge -> challenge.id.equalsIgnoreCase(id)).findFirst();
    }

    public String getName(Player player) {
        return this.name.getValue(player);
    }

    public List<String> getLore(Player player) {
        final String str = this.lore.getValue(player);
        final String[] splitLore = str.split("\n");

        return new ArrayList<>(Arrays.asList(splitLore));
    }

    public RunnerChallengeModel getModel() {
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
