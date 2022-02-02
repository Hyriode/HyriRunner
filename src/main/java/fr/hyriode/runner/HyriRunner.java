package fr.hyriode.runner;

import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGameType;
import fr.hyriode.hyrame.language.IHyriLanguageManager;
import fr.hyriode.hyriapi.HyriAPI;
import fr.hyriode.runner.api.HyriRunnerApi;
import fr.hyriode.runner.config.HyriRunnerConfig;
import fr.hyriode.runner.game.HyriRunnerGame;
import fr.hyriode.runner.game.HyriRunnerGameType;
import fr.hyriode.runner.game.gamemap.HyriRunnerGameMap;
import fr.hyriode.runner.game.gamemap.HyriRunnerMapGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class HyriRunner extends JavaPlugin {

    public static final String NAME = "TheRunner";
    private IHyrame hyrame;
    private HyriAPI hyriApi;
    private HyriRunnerGame game;
    private HyriRunnerGameMap gameMap;
    private static IHyriLanguageManager languageManager;
    private HyriRunnerApi api;
    private HyriRunnerConfig configuration;


    @Override
    public void onEnable() {
        this.hyrame = HyrameLoader.load(new HyriRunnerProvider(this));
        this.hyriApi = HyriAPI.get();
        languageManager = this.hyrame.getLanguageManager();
        this.api = new HyriRunnerApi(hyriApi.getRedisConnection().getPool());
        this.gameMap = new HyriRunnerGameMap("map");
        this.gameMap.create();
        this.gameMap.patch();
        this.configuration = new HyriRunnerConfig(this);
        this.configuration.create();
        this.configuration.load();
        HyriRunnerGameType.setWithName(getConfiguration().getGameType());
        this.game = new HyriRunnerGame(this.hyrame, this);
        this.hyrame.getGameManager().registerGame(this.game);
        this.getGame().setDamage(false);
        this.getGame().setPvp(false);
        this.getGame().setJoinable(false);
        HyriRunnerMapGenerator generator = new HyriRunnerMapGenerator(Bukkit.getWorld(this.getGameMap().getName()), this);
        generator.setFuture(new HyriRunnerMapGenerator.CompletableFuture() {
            @Override
            public void onComplete(World world) {
                getGame().setJoinable(true);
            }

            @Override
            public void onFail(World world) {
                log(Level.SEVERE, "Error during generation of the game map. Shutdown server.");
                getServer().shutdown();
            }
        });
        generator.generate(1000 / 16, true);
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

    public HyriAPI getHyriApi() {
        return this.hyriApi;
    }

    public HyriRunnerGame getGame() {
        return this.game;
    }

    public HyriRunnerGameMap getGameMap() {
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
}
