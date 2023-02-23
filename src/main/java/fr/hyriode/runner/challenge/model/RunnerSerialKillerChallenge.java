package fr.hyriode.runner.challenge.model;

import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.hyrame.game.event.player.HyriGameDeathEvent;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.RunnerChallengeModel;
import fr.hyriode.runner.challenge.RunnerChallenge;
import fr.hyriode.runner.challenge.RunnerChallengeDifficulty;
import fr.hyriode.runner.game.RunnerGame;
import fr.hyriode.runner.game.RunnerGamePlayer;
import fr.hyriode.runner.game.phase.RunnerPhase;
import fr.hyriode.runner.game.phase.RunnerPhaseTriggeredEvent;
import org.bukkit.Material;

public class RunnerSerialKillerChallenge extends RunnerChallenge {

    private int totalPlayers;

    private final HyriRunner plugin;

    public RunnerSerialKillerChallenge(HyriRunner plugin) {
        super(RunnerChallengeModel.SERIAL_KILLER, "serial-killer", Material.DIAMOND_SWORD, RunnerChallengeDifficulty.MEDIUM);
        this.plugin = plugin;
    }

    @HyriEventHandler
    public void onPhaseTriggered(RunnerPhaseTriggeredEvent event) {
        if (event.getPhase() != RunnerPhase.PVP) {
            return;
        }

        final RunnerGame game = event.getGame();

        for (RunnerGamePlayer gamePlayer : game.getPlayers()) {
            if (gamePlayer.isDead()) {
                continue;
            }

            this.totalPlayers++;
        }
    }

    @Override
    public boolean isValid(RunnerGamePlayer gamePlayer) {
        return this.plugin.getGame().isPhase(RunnerPhase.PVP) && this.totalPlayers - 1 == gamePlayer.getKills();
    }

}
