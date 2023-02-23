package fr.hyriode.runner.bonus;

import fr.hyriode.api.event.HyriEvent;
import fr.hyriode.runner.game.RunnerGame;
import fr.hyriode.runner.game.RunnerGamePlayer;

/**
 * Created by AstFaster
 * on 21/08/2022 at 20:07
 */
public class RunnerBonusSelectedEvent extends HyriEvent {

    private final RunnerGame game;
    private final RunnerGamePlayer gamePlayer;
    private final RunnerBonus bonus;

    public RunnerBonusSelectedEvent(RunnerGame game, RunnerGamePlayer gamePlayer, RunnerBonus bonus) {
        this.game = game;
        this.gamePlayer = gamePlayer;
        this.bonus = bonus;
    }

    public RunnerGame getGame() {
        return this.game;
    }

    public RunnerGamePlayer getGamePlayer() {
        return this.gamePlayer;
    }

    public RunnerBonus getBonus() {
        return this.bonus;
    }

}
