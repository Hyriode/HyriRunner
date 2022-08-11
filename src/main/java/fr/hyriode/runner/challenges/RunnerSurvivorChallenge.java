package fr.hyriode.runner.challenges;

import fr.hyriode.api.color.HyriChatColor;
import fr.hyriode.hyrame.game.team.HyriGameTeamColor;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.challenges.HyriRunnerChallengeModel;
import fr.hyriode.runner.game.RunnerGamePlayer;
import org.bukkit.DyeColor;
import org.bukkit.Material;

/**
 * Project: Hyriode
 * Created by Akkashi
 * on 30/06/2022 at 11:41
 */
public class RunnerSurvivorChallenge extends RunnerChallenge {
    public RunnerSurvivorChallenge(HyriRunner pl) {
        super(
                HyriRunnerChallengeModel.SURVIVOR,
                "challenge.survivor.name",
                new String[]{"challenge.survivor.lore.1", "challenge.survivor.lore.2"},
                Material.REDSTONE,
                2
        );
    }

    @Override
    public boolean getCondition(RunnerGamePlayer player) {
        return !player.isDamagesTaken();
    }

    @Override
    public void getReward(RunnerGamePlayer player) {
        this.sendSuccessMessage(player);
    }
}
