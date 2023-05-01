package fr.hyriode.runner.game.teleport;

import fr.hyriode.hyrame.utils.block.Cuboid;
import fr.hyriode.runner.HyriRunner;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class RunnerCage {

    /** A magic value obtained with 1500 (the border size) divided by 900 (the cage position) */
    private static final double MULTIPLIER = 0.6;

    private Cuboid cuboid;

    private final Location location;
    private final Location center;

    public RunnerCage() {
        final World world = Bukkit.getWorld(HyriRunner.GAME_MAP);
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        final int border = (int) (world.getWorldBorder().getSize() / 2);
        final int distance = (int) (border * MULTIPLIER);

        final Supplier<Integer> positionProvider = () -> {
            final int result = random.nextInt(distance - 20, distance);

            return random.nextBoolean() ? result * -1 : result;
        };

        final int x = positionProvider.get();
        final int z = positionProvider.get();
        final int highestBlock = world.getHighestBlockYAt(x, z);
        final int y = highestBlock + ((256 - highestBlock) / 3);

        this.location = new Location(world, x, y, z);
        this.center = this.location.clone().subtract(0, 1, 0);
    }

    public void create() {
        this.cuboid = new Cuboid(this.center.clone().add(4, 4, 4), this.center.clone().add(-4, 0, -4));

        for (Block block : this.cuboid) {
            block.setType(Material.DIRT);
        }

        for (Block block : this.cuboid.outset(Cuboid.CuboidDirection.BOTH, -1)) {
            block.setType(Material.AIR);
        }
    }

    public void remove() {
        if (this.cuboid == null) {
            throw new IllegalStateException("The cage has not been created!");
        }

        for (Block block : this.cuboid) {
            block.breakNaturally();
        }
    }

    public Location getLocation() {
        return this.location;
    }

}
