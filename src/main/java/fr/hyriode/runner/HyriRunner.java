package fr.hyriode.runner;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.server.IHyriServer;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.world.generator.HyriWorldGenerator;
import fr.hyriode.hyrame.world.generator.HyriWorldSettings;
import fr.hyriode.runner.challenge.RunnerChallenge;
import fr.hyriode.runner.config.RunnerConfig;
import fr.hyriode.runner.game.RunnerGame;
import fr.hyriode.runner.game.host.category.RunnerHostMainCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class HyriRunner extends JavaPlugin {

    public static final String NAME = "TheRunner";
    public static final String GAME_MAP = "map";

    private IHyrame hyrame;

    private RunnerGame game;
    private RunnerConfig configuration;

    @Override
    public void onEnable() {
        this.hyrame = HyrameLoader.load(new HyriRunnerProvider(this));
        this.configuration = HyriAPI.get().getServer().getConfig(RunnerConfig.class);
        this.game = new RunnerGame(this.hyrame, this);
        this.hyrame.getGameManager().registerGame(() -> this.game);

        RunnerChallenge.registerChallenges(this);

        if (HyriAPI.get().getServer().getAccessibility().equals(HyggServer.Accessibility.HOST)) {
            this.hyrame.getHostController().addCategory(25, new RunnerHostMainCategory());
        }

        HyriWorldGenerator worldGenerator =
                new HyriWorldGenerator(this, new HyriWorldSettings("map"), 1000, world -> HyriAPI.get().getServer().setState(HyggServer.State.READY));
        HyriWorldGenerator.COMMON_PATCHED_BIOMES.forEach(worldGenerator::patchBiomes);

        worldGenerator.start();
    }

    @Override
    public void onDisable() {
        log("Stopping " + NAME + "...");

        this.hyrame.getGameManager().unregisterGame(game);
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

    public RunnerConfig getConfiguration() {
        return configuration;
    }

}
