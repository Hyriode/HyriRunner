package fr.hyriode.runner.config;

import fr.hyriode.api.config.IHyriConfig;
import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;

/**
 * Project: HyriRunner
 * Created by Akkashi
 * on 23/04/2022 at 09:31
 */
public class RunnerConfig implements IHyriConfig {

    private final HyriWaitingRoom.Config waitingRoom;

    public RunnerConfig(HyriWaitingRoom.Config waitingRoom) {
        this.waitingRoom = waitingRoom;
    }

    public HyriWaitingRoom.Config getWaitingRoom() {
        return this.waitingRoom;
    }

}
