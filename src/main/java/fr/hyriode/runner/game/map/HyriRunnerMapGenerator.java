package fr.hyriode.runner.game.map;

import fr.hyriode.runner.HyriRunner;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class HyriRunnerMapGenerator {

    private BukkitTask task;
    private List<HyriRunnerMapChunk> chunks;

    private int lastShow;
    private int numberChunk;

    private CompletableFuture completableFuture;

    private final HyriRunnerMap map;

    private final World world;
    private final HyriRunner plugin;

    public HyriRunnerMapGenerator(World world, HyriRunner plugin) {
        this.world = world;
        this.plugin = plugin;
        this.map = this.plugin.getGameMap();
    }

    public void generate(int size, boolean initialize) {

        this.task = Bukkit.getScheduler().runTaskTimer(this.plugin, new Runnable()
        {
            private int todo = ((size * 2) * (size * 2)) / 256;
            private int x = -size;
            private int z = -size;

            @Override
            public void run()
            {
                int i = 0;

                while (i < 50)
                {
                    world.getChunkAt(world.getBlockAt(this.x, 64, this.z)).load(true);

                    int percentage = numberChunk * 100 / todo;
                    if (percentage > lastShow && percentage % 10 == 0)
                    {
                        lastShow = percentage;
                        plugin.getLogger().info("Loading chunks (" + percentage + "%)");
                    }

                    this.z += 16;

                    if (this.z >= size)
                    {
                        this.z = -size;
                        this.x += 16;
                    }

                    if (this.x >= size)
                    {
                        task.cancel();
                        completableFuture.onComplete(world);
                        return;
                    }

                    numberChunk++;
                    i++;
                }
            }
        }, 1L, 1L);

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