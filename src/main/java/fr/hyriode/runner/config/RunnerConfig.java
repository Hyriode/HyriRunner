package fr.hyriode.runner.config;

import fr.hyriode.hyrame.configuration.HyriConfigurationEntry;
import fr.hyriode.hyrame.configuration.IHyriConfiguration;
import fr.hyriode.runner.HyriRunner;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.function.Supplier;

public class RunnerConfig implements IHyriConfiguration {

    private static final Supplier<Location> DEFAULT_LOCATION = () -> new Location(Bukkit.getWorld("world"), 0, 150, 0, 0, 0);

    private Location spawn;
    private final HyriConfigurationEntry.LocationEntry spawnEntry;

    private String gameType;
    private final HyriConfigurationEntry.StringEntry gameTypeEntry;

    private final FileConfiguration config;
    private final HyriRunner plugin;

    public RunnerConfig(HyriRunner plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();

        this.spawn = DEFAULT_LOCATION.get();
        this.spawnEntry = new HyriConfigurationEntry.LocationEntry("spawn", this.config);

        this.gameType = "solo";
        this.gameTypeEntry = new HyriConfigurationEntry.StringEntry("game-type", this.config);

        this.create();
        this.load();
    }

    @Override
    public void create() {
        this.spawnEntry.setDefault(this.spawn);

        this.gameTypeEntry.setDefault(this.gameType);

        this.plugin.saveConfig();
    }

    @Override
    public void load() {
        HyriRunner.log("Loading configuration...");

        this.spawn = this.spawnEntry.get();
        this.gameType = this.gameTypeEntry.get();
    }

    @Override
    public void save() {
        HyriRunner.log("Saving configuration...");

        this.spawnEntry.set(this.spawn);
        this.gameTypeEntry.set(this.gameType);

        this.plugin.saveConfig();
    }

    @Override
    public FileConfiguration getConfig() {
        return this.config;
    }

    public Location getSpawn() {
        return spawn;
    }

    public String getGameType() {
        return gameType;
    }

}
