package fr.hyriode.runner;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.world.IHyriWorld;
import fr.hyriode.api.world.generation.IWorldGenerationAPI;
import fr.hyriode.api.world.generation.WorldGenerationType;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;
import fr.hyriode.hyrame.utils.LocationWrapper;
import fr.hyriode.hyrame.world.generator.HyriWorldGenerator;
import fr.hyriode.hyrame.world.generator.HyriWorldSettings;
import fr.hyriode.runner.challenge.RunnerChallenge;
import fr.hyriode.runner.config.RunnerConfig;
import fr.hyriode.runner.game.RunnerGame;
import fr.hyriode.runner.game.host.category.RunnerHostMainCategory;
import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class HyriRunner extends JavaPlugin {

    public static final String NAME = "TheRunner";
    public static final String GAME_MAP = "map";

    private static HyriRunner instance;

    private IHyrame hyrame;

    private RunnerGame game;
    private RunnerConfig configuration;

    @Override
    public void onEnable() {
        instance = this;

        // Load world
        final IWorldGenerationAPI worldGenerationAPI = HyriAPI.get().getWorldGenerationAPI();
        final List<IHyriWorld> worlds = worldGenerationAPI.getWorlds(WorldGenerationType.THE_RUNNER);

        Collections.shuffle(worlds);

        if (worlds.size() == 0) { // Security
            final HyriWorldGenerator worldGenerator = new HyriWorldGenerator(this, new HyriWorldSettings(GAME_MAP), 1000, world -> HyriAPI.get().getServer().setState(HyggServer.State.READY));

            HyriWorldGenerator.COMMON_PATCHED_BIOMES.forEach(worldGenerator::patchBiomes);

            worldGenerator.start();
            return;
        }

        final IHyriWorld world = worlds.get(0);

        world.load(new File(GAME_MAP));

        new WorldCreator(GAME_MAP).createWorld();

        worldGenerationAPI.removeWorld(WorldGenerationType.THE_RUNNER, world.getName());

        // Start Hyrame and game
        this.hyrame = HyrameLoader.load(new RunnerProvider(this));
        this.configuration = HyriAPI.get().getConfig().isDevEnvironment() ?
                new RunnerConfig(new HyriWaitingRoom.Config(
                        new LocationWrapper(0, 190, 0),
                        new LocationWrapper(0, 0, 0),
                        new LocationWrapper(0, 0, 0),
                        new LocationWrapper(0, 0, 0))):
                HyriAPI.get().getServer().getConfig(RunnerConfig.class);
        this.game = new RunnerGame(this.hyrame, this);
        this.hyrame.getGameManager().registerGame(() -> this.game);

        RunnerChallenge.registerChallenges(this);

        if (HyriAPI.get().getServer().getAccessibility().equals(HyggServer.Accessibility.HOST)) {
            this.hyrame.getHostController().addCategory(25, new RunnerHostMainCategory());
        }

        HyriAPI.get().getServer().setState(HyggServer.State.READY);
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

    public static HyriRunner get() {
        return instance;
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
