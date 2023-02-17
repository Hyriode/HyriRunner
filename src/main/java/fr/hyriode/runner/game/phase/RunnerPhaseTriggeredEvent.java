package fr.hyriode.runner.game.phase;

import fr.hyriode.api.event.HyriEvent;
import fr.hyriode.runner.game.RunnerGame;

/**
 * Created by AstFaster
 * on 21/08/2022 at 20:02
 */
public class RunnerPhaseTriggeredEvent extends HyriEvent {

    private final RunnerGame game;
    private final RunnerPhase phase;

    public RunnerPhaseTriggeredEvent(RunnerGame game, RunnerPhase phase) {
        this.game = game;
        this.phase = phase;
    }

    public RunnerGame getGame() {
        return this.game;
    }

    public RunnerPhase getPhase() {
        return this.phase;
    }

}
