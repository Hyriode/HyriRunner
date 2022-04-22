package fr.hyriode.runner;

import fr.hyriode.api.server.IHyriServer;
import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.language.IHyriLanguageManager;
import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.world.HyriWorldSettings;
import fr.hyriode.hyrame.world.generator.HyriWorldGenerator;
import fr.hyriode.runner.api.HyriRunnerApi;
import fr.hyriode.runner.challenges.RunnerChallenge;
import fr.hyriode.runner.config.RunnerConfig;
import fr.hyriode.runner.game.RunnerGame;
import fr.hyriode.runner.game.RunnerGameType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class HyriRunner extends JavaPlugin {

    public static final String NAME = "TheRunner";
    public static final String GAME_MAP = "map";

    private static IHyriLanguageManager languageManager;

    private IHyrame hyrame;

    private RunnerGame game;
    private HyriRunnerApi api;
    private RunnerConfig configuration;

    @Override
    public void onEnable() {
        this.hyrame = HyrameLoader.load(new HyriRunnerProvider(this));

        languageManager = this.hyrame.getLanguageManager();
        IHyriLanguageManager.Provider.registerInstance(() -> this.hyrame.getLanguageManager());

        this.api = new HyriRunnerApi(HyriAPI.get().getRedisConnection().getPool());
        this.api.start();
        this.configuration = new RunnerConfig(this);

        RunnerGameType.setWithName(this.configuration.getGameType());

        this.game = new RunnerGame(this.hyrame, this);
        this.hyrame.getGameManager().registerGame(() -> this.game);
        RunnerChallenge.registerChallenges(this);

        HyriWorldGenerator worldGenerator = new HyriWorldGenerator(this, new HyriWorldSettings("map"), 1000, world -> {
            HyriAPI.get().getServer().setState(IHyriServer.State.READY);
        });
        HyriWorldGenerator.COMMON_PATCHED_BIOMES.forEach(worldGenerator::patchBiomes);
        worldGenerator.start();
    }

    @Override
    public void onDisable() {
        log("Stopping " + NAME + "...");

        this.hyrame.getGameManager().unregisterGame(game);
        this.api.stop();
    }

    public static void log(Level level, String message) {
        String prefix = ChatColor.RED + "[" + NAME + "] ";

        if (level == Level.SEVERE) {
            prefix += ChatColor.RED;
        } else if (level == Level.WARNING) {
            prefix += ChatColor.YELLOW;
        } else {
            prefix += ChatColor.RESET;
        }
        Bukkit.getConsoleSender().sendMessage(prefix + message);
    }

    public static void log(String msg) {
        log(Level.INFO, msg);
    }

    public IHyrame getHyrame() {
        return this.hyrame;
    }

    public RunnerGame getGame() {
        return this.game;
    }

    public static IHyriLanguageManager getLanguageManager() {
        return languageManager;
    }

    public HyriRunnerApi getApi() {
        return api;
    }

    public RunnerConfig getConfiguration() {
        return configuration;
    }

}
