package fr.hyriode.runner.challenges;

import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.challenges.HyriRunnerChallengeModel;
import fr.hyriode.runner.game.HyriRunnerGamePlayer;
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
    private final int id;
    private final Material icon;

    public HyriRunnerChallenge(HyriRunnerChallengeModel model, String nameKey, String[] loreKey, int id, Material icon) {
        this.model = model;
        this.key = nameKey;
        this.loreKey = loreKey;
        this.id = id;
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

    public int getId() {
        return id;
    }

    public Material getIcon() {
        return icon;
    }
}
