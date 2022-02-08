package fr.hyriode.runner.challenges;

import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyriapi.settings.HyriLanguage;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.challenges.HyriRunnerChallengeModel;
import fr.hyriode.runner.game.HyriRunnerGamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class HyriRunnerChallenge {

    protected static final Map<Class<? extends HyriRunnerChallenge>, HyriRunnerChallenge> challengesMap = new HashMap<>();

    private final HyriRunnerChallengeModel model;
    private final String key;
    private final String[] loreKey;
    private final Material icon;

    public HyriRunnerChallenge(HyriRunnerChallengeModel model, String nameKey, String[] loreKey, Material icon) {
        this.model = model;
        this.key = nameKey;
        this.loreKey = loreKey;
        this.icon = icon;
    }

    public abstract boolean getCondition(HyriRunnerGamePlayer player);
    public abstract void getReward(HyriRunnerGamePlayer player);

    public static void registerChallenges(HyriRunner pl) {
        HyriRunner.log("Registering challenges...");

        /*  Add challenges here  */
        new HyriRunnerFirstChallenge(pl);
        new HyriRunnerWarriorChallenge(pl);
        new HyriRunnerSerialKillerChallenge(pl);
        new HyriRunnerNoBlockPlaced(pl);
        new HyriRunnerArrivedChallenge(pl);
        new HyriRunnerLastChallenge(pl);

        if(!challengesMap.isEmpty()) {
            challengesMap.values().forEach(challenge -> HyriRunner.log("Registered challenge:" +challenge.getModel().name()));

            HyriRunner.log("Registered " +challengesMap.size()+ " challenges!");
        }
    }

    public static Optional<HyriRunnerChallenge> getWithModel(HyriRunnerChallengeModel model) {
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

    public void sendSuccessMessage(HyriRunnerGamePlayer gamePlayer) {
        final HyriLanguageMessage message = new HyriLanguageMessage("message.challenge-success")
                .addValue(HyriLanguage.FR, ChatColor.GOLD+ "" + ChatColor.BOLD+ "Bien joué ! " + ChatColor.YELLOW+ "Vous avez réussi le défi : " +ChatColor.AQUA+ "%challenge%")
                .addValue(HyriLanguage.EN, ChatColor.GOLD+ "" + ChatColor.BOLD+ "Well played! " + ChatColor.YELLOW+ "You completed the challenge: " +ChatColor.AQUA+ "%challenge%");
        gamePlayer.sendMessage(message.getForPlayer(gamePlayer.getPlayer())
                .replace("%challenge%", HyriRunner.getLanguageManager().getMessage(gamePlayer.getChallenge().getKey()).getForPlayer(gamePlayer.getPlayer()))
        );
    }
}
