package fr.hyriode.runner.challenges;

import fr.hyriode.api.settings.HyriLanguage;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.challenges.HyriRunnerChallengeModel;
import fr.hyriode.runner.game.RunnerGamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class RunnerChallenge {

    protected static final Map<Class<? extends RunnerChallenge>, RunnerChallenge> challengesMap = new HashMap<>();

    private final HyriRunnerChallengeModel model;
    private final String key;
    private final String[] loreKey;
    private final int difficulty;
    private final Material icon;

    public RunnerChallenge(HyriRunnerChallengeModel model, String nameKey, String[] loreKey, Material icon, int difficulty) {
        this.model = model;
        this.key = nameKey;
        this.loreKey = loreKey;
        this.icon = icon;
        this.difficulty = difficulty;
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

        if (!challengesMap.isEmpty()) {
            challengesMap.values().forEach(challenge -> HyriRunner.log("Registered challenge:" + challenge.getModel().name()));

            HyriRunner.log("Registered " + challengesMap.size() + " challenges!");
        }
    }

    public static Optional<RunnerChallenge> getWithModel(HyriRunnerChallengeModel model) {
        return challengesMap.values().stream().filter(challenge -> challenge.model.equals(model)).findFirst();
    }

    public HyriRunnerChallengeModel getModel() {
        return this.model;
    }

    public String getKey() {
        return this.key;
    }

    public String[] getLore() {
        return loreKey;
    }

    public Material getIcon() {
        return icon;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getDifficultyAsString() {
        if (this.difficulty == 1) {
            return ChatColor.YELLOW + "✮" + ChatColor.GRAY + "✮✮";

        } else if (this.difficulty == 2) {
            return ChatColor.YELLOW + "✮✮" + ChatColor.GRAY + "✮";

        } else if (this.difficulty == 3) {
            return ChatColor.YELLOW + "✮✮✮";

        } else {
            return "";
        }
    }

    public void sendSuccessMessage(RunnerGamePlayer gamePlayer) {
        final HyriLanguageMessage message = new HyriLanguageMessage("message.challenge-success")
                .addValue(HyriLanguage.FR, ChatColor.GOLD + "" + ChatColor.BOLD + "Bien joué ! " + ChatColor.YELLOW + "Vous avez réussi le défi : " + ChatColor.AQUA + "%challenge%")
                .addValue(HyriLanguage.EN, ChatColor.GOLD + "" + ChatColor.BOLD + "Well played! " + ChatColor.YELLOW + "You completed the challenge: " + ChatColor.AQUA + "%challenge%");
        gamePlayer.sendMessage(message.getForPlayer(gamePlayer.getPlayer())
                .replace("%challenge%", HyriRunner.getLanguageManager().getMessage(gamePlayer.getChallenge().getKey()).getForPlayer(gamePlayer.getPlayer()))
        );
    }
}
