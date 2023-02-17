package fr.hyriode.runner.challenge.model;

import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.RunnerChallengeModel;
import fr.hyriode.runner.bonus.RunnerBonus;
import fr.hyriode.runner.bonus.RunnerBonusSelectedEvent;
import fr.hyriode.runner.challenge.RunnerChallenge;
import fr.hyriode.runner.challenge.RunnerChallengeDifficulty;
import fr.hyriode.runner.game.RunnerGamePlayer;
import org.bukkit.Material;

import java.util.UUID;

public class RunnerWarriorChallenge extends RunnerChallenge {

    private UUID first;
    private boolean noBonus;

    private final HyriRunner plugin;

    public RunnerWarriorChallenge(HyriRunner plugin) {
        super(RunnerChallengeModel.WARRIOR, "warrior", Material.IRON_AXE, RunnerChallengeDifficulty.NORMAL);
        this.plugin = plugin;
    }

    @HyriEventHandler
    public void onBonusSelected(RunnerBonusSelectedEvent event) {
        this.first = event.getGamePlayer().getUniqueId();
        this.noBonus = event.getBonus() == RunnerBonus.NOTHING;
    }

    @Override
    public boolean isValid(RunnerGamePlayer gamePlayer) {
        final HyriGameTeam winner = this.plugin.getGame().getWinner();

        return winner != null && winner.contains(gamePlayer) && this.first == gamePlayer.getUniqueId() && this.noBonus;
    }

}
