package fr.hyriode.runner.game.map;

import fr.hyriode.runner.HyriRunner;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class HyriRunnerMapGenerator {

    private int actualChunk;
    private int totalChunk;
    private int remainingChunk;

    private List<HyriRunnerMapChunk> chunks;

    private CompletableFuture completableFuture;

    private final HyriRunnerMap map;

    private final World world;
    private final HyriRunner plugin;

    public HyriRunnerMapGenerator(World world, HyriRunner plugin) {
        this.world = world;
        this.plugin = plugin;
        this.map = this.plugin.getGameMap();
    }

    public void generate(int radius, boolean initialize) {
        if (initialize) {
            final Chunk origin = world.getChunkAt(0, 0);

            this.chunks = this.map.getChunksAround(origin, radius);
            this.remainingChunk = chunks.size();
            this.totalChunk = chunks.size();
        }

        final Instant before = Instant.now();
        for (int i = this.actualChunk; i < this.chunks.size(); i++) {
            final HyriRunnerMapChunk mapChunk = chunks.get(i);

            this.remainingChunk--;
            this.actualChunk++;

            final Chunk chunk = world.getChunkAt(mapChunk.getX(), mapChunk.getZ());

            chunk.load(true);

            if (Duration.between(before, Instant.now()).toMillis() >= 450) {
                final int percentage = (this.totalChunk - this.remainingChunk) * 100 / this.totalChunk;

                System.out.println("The game map (" + ((this.totalChunk - this.remainingChunk) / 16) + "/" + (this.totalChunk / 16) + ") is generated at " + percentage + "%");

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        generate(radius, false);
                    }
                }.runTaskLater(plugin, 7L);
                return;
            }
        }
        completableFuture.onComplete(world);
    }

    public HyriRunnerMapGenerator setFuture(CompletableFuture completableFuture) {
        this.completableFuture = completableFuture;
        return this;
    }

    public interface CompletableFuture {

        void onComplete(World world);

        void onFailure(World world);

    }
}