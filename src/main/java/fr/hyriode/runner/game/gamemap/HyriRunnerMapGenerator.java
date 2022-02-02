package fr.hyriode.runner.game.gamemap;

import fr.hyriode.runner.HyriRunner;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class HyriRunnerMapGenerator {

    private HyriRunner plugin;
    private World world;
    private int actual;
    private int totalChunk;
    private int remainingChunk;
    private ArrayList<HyriRunnerMapChunks> chunks;
    private CompletableFuture completableFuture = null;

    public HyriRunnerMapGenerator(World world, HyriRunner plugin) {
        this.world = world;
        this.plugin = plugin;
    }

    private ArrayList<HyriRunnerMapChunks> around(Chunk origin, int radius) {

        World world = origin.getWorld();

        int length = (radius * 2) + 1;
        ArrayList<HyriRunnerMapChunks> chunks = new ArrayList<>(length * length);

        int cX = origin.getX();
        int cZ = origin.getZ();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                chunks.add(new HyriRunnerMapChunks(cX + x, cZ + z));
            }
        }
        return chunks;
    }

    public void generate(int radius, boolean b) {
        if (b) {
            Chunk origin = world.getChunkAt(0, 0);
            chunks = around(origin, radius);
            remainingChunk = chunks.size();
            totalChunk = chunks.size();
        }
        Instant before = Instant.now();
        for (int i = actual; i < chunks.size(); i++) {
            HyriRunnerMapChunks chunksCoords = chunks.get(i);
            remainingChunk--;
            actual++;
            Chunk chunk = world.getChunkAt(chunksCoords.getX(), chunksCoords.getZ());
            chunk.load(true);
            chunk.load(false);
            chunk = null;
            if (Duration.between(before, Instant.now()).toMillis() >= 450) {
                int r = this.totalChunk - remainingChunk;
                int pourcentage = r * 100 / this.totalChunk;
                System.out.println("The game map (" + (this.totalChunk / 16) + "/" + (this.totalChunk / 16) + ") is generated at " + pourcentage + "%");
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
        public void onComplete(World world);

        public void onFail(World world);
    }
}