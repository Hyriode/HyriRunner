package fr.hyriode.runner.config;

import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;
import fr.hyriode.hystia.api.config.IConfig;

/**
 * Project: HyriRunner
 * Created by Akkashi
 * on 23/04/2022 at 09:31
 */
public class RunnerConfig implements IConfig {

    private final HyriWaitingRoom.Config waitingRoom;

    public RunnerConfig(HyriWaitingRoom.Config waitingRoom) {
        this.waitingRoom = waitingRoom;
    }

    public HyriWaitingRoom.Config getWaitingRoom() {
        return this.waitingRoom;
    }

}
