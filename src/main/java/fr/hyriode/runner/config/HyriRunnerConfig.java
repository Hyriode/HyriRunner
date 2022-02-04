package fr.hyriode.runner.config;

import fr.hyriode.hyrame.configuration.HyriConfigurationEntry;
import fr.hyriode.hyrame.configuration.IHyriConfiguration;
import fr.hyriode.runner.HyriRunner;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.function.Supplier;

public class HyriRunnerConfig implements IHyriConfiguration {

    private static final Supplier<Location> DEFAULT_LOCATION = () -> new Location(Bukkit.getWorld("world"), 0, 150, 0, 0, 0);

    private Location spawn;
    private final HyriConfigurationEntry.LocationEntry spawnEntry;

    private int minPlayers;
    private final HyriConfigurationEntry.IntegerEntry minPlayersEntry;

    private String gameType;
    private final HyriConfigurationEntry.StringEntry gameTypeEntry;

    private final FileConfiguration config;
    private final HyriRunner plugin;

    public HyriRunnerConfig(HyriRunner plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();

        this.spawn = DEFAULT_LOCATION.get();
        this.spawnEntry = new HyriConfigurationEntry.LocationEntry("spawn", this.config);

        this.minPlayers = 2;
        this.minPlayersEntry = new HyriConfigurationEntry.IntegerEntry("min-players", this.config);

        this.gameType = "solo";
        this.gameTypeEntry = new HyriConfigurationEntry.StringEntry("game-type", this.config);

        this.create();
        this.load();
    }

    @Override
    public void create() {
        this.spawnEntry.setDefault(this.spawn);

        this.minPlayersEntry.setDefault(this.minPlayers);

        this.gameTypeEntry.setDefault(this.gameType);

        this.plugin.saveConfig();
    }

    @Override
    public void load() {
        HyriRunner.log("Loading configuration...");

        this.spawn = this.spawnEntry.get();
        this.minPlayers = this.minPlayersEntry.get();
        this.gameType = this.gameTypeEntry.get();
    }

    @Override
    public void save() {
        HyriRunner.log("Saving configuration...");

        this.spawnEntry.set(this.spawn);
        this.minPlayersEntry.set(this.minPlayers);
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

    public int getMinPlayers() {
        return minPlayers;
    }

    public String getGameType() {
        return gameType;
    }

}
