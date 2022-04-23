package fr.hyriode.runner.config;

import fr.hyriode.hyrame.utils.LocationWrapper;
import fr.hyriode.hystia.api.config.IConfig;

/**
 * Project: HyriRunner
 * Created by Akkashi
 * on 23/04/2022 at 09:31
 */
public class RunnerConfig implements IConfig {

    private final LocationWrapper spawn;

    public RunnerConfig(LocationWrapper spawn) {
        this.spawn = spawn;
    }

    public LocationWrapper getSpawn() {
        return this.spawn;
    }
}
