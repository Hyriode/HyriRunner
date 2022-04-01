package fr.hyriode.runner;

import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.language.IHyriLanguageManager;
import fr.hyriode.api.HyriAPI;
import fr.hyriode.runner.api.HyriRunnerApi;
import fr.hyriode.runner.challenges.HyriRunnerChallenge;
import fr.hyriode.runner.config.HyriRunnerConfig;
import fr.hyriode.runner.game.HyriRunnerGame;
import fr.hyriode.runner.game.HyriRunnerGameType;
import fr.hyriode.runner.game.map.HyriRunnerMap;
import fr.hyriode.runner.game.map.HyriRunnerMapGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class HyriRunner extends JavaPlugin {

    public static final String NAME = "TheRunner";

    private static IHyriLanguageManager languageManager;

    private IHyrame hyrame;
    private HyriRunnerGame game;
    private HyriRunnerMap gameMap;
    private HyriRunnerApi api;
    private HyriRunnerConfig configuration;

    private HyriRunnerMapGenerator generator;

    @Override
    public void onEnable() {
        this.hyrame = HyrameLoader.load(new HyriRunnerProvider(this));
        languageManager = this.hyrame.getLanguageManager();
        this.api = new HyriRunnerApi(HyriAPI.get().getRedisConnection().getPool());
        this.api.start();
        this.gameMap = new HyriRunnerMap("map");
        this.configuration = new HyriRunnerConfig(this);

        HyriRunnerGameType.setWithName(this.configuration.getGameType());

        this.game = new HyriRunnerGame(this.hyrame, this);
        this.hyrame.getGameManager().registerGame(() -> this.game);
        HyriRunnerChallenge.registerChallenges(this);

        this.setupMapGenerator();
    }

    private void setupMapGenerator() {
        final HyriRunnerMapGenerator.CompletableFuture completableFuture = new HyriRunnerMapGenerator.CompletableFuture() {

            @Override
            public void onComplete(World world) {
                getGame().setAccessible(true);
            }

            @Override
            public void onFailure(World world) {
                log(Level.SEVERE, "Error during generation of the game map. Shutdown server.");
                Bukkit.getServer().shutdown();
            }
        };

        this.generator = new HyriRunnerMapGenerator(Bukkit.getWorld(this.gameMap.getName()), this);
        this.generator.setFuture(completableFuture);
        this.generator.generate(1000, true);
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

    public HyriRunnerGame getGame() {
        return this.game;
    }

    public HyriRunnerMap getGameMap() {
        return gameMap;
    }

    public static IHyriLanguageManager getLanguageManager() {
        return languageManager;
    }

    public HyriRunnerApi getApi() {
        return api;
    }

    public HyriRunnerConfig getConfiguration() {
        return configuration;
    }

    public HyriRunnerMapGenerator getGenerator() {
        return generator;
    }
}
