package fr.hyriode.runner.game;

import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.utils.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HyriPositionCalculator {

    private static List<Cage> CAGES = new ArrayList<>();

    private Location location;
    private HyriRunner plugin;
    private Random random = new Random();

    public HyriPositionCalculator(HyriRunner plugin) {
        this.plugin = plugin;
        int y = 150;
        int max;
        int min;
        max = 961;
        min = 933;

        int xI = min + (int) (Math.random() * ((max - min) + 1));
        int zI = min + (int) (Math.random() * ((max - min) + 1));

        int varI = random.nextInt(2);
        if (varI == 1) {
            xI = xI * -1;
        }
        varI = random.nextInt(2);
        if (varI == 1) {
            zI = zI * -1;
        }
        Location center = new Location(Bukkit.getWorld(plugin.getGameMap().getName()), 0, y, 0);
        location = center.clone().add(xI, 0, zI);
    }

    public Location getLocation() {
        return location;
    }

    public Location getCuboidCenter() {
        Location cLoc = location.clone();
        cLoc.setY(location.getY() - 1);
        return cLoc;
    }

    public void removeCages() {
        CAGES.forEach(Cage::removeCage);
    }

    public static class Cage {

        private Location cageCenter;

        public Cage(Location cageCenter) {
            this.cageCenter = cageCenter;
            CAGES.add(this);
        }

        public void setCage() {

            Location pos1 = cageCenter.clone().add(3, 0, -3);
            Location pos2 = cageCenter.clone().add(-3, 4, 3);
            Location littlePos1 = cageCenter.clone().add(2, 1, -2);
            Location littlePos2 = cageCenter.clone().add(-2, 3, 2);

            Cuboid cuboid = new Cuboid(pos1, pos2);
            Cuboid littleCuboid = new Cuboid(littlePos1, littlePos2);

            for (Block blocks : cuboid) {
                blocks.setType(Material.GLASS, true);
            }
            for (Block blocks : littleCuboid) {
                blocks.setType(Material.AIR, true);
            }
        }

        public void removeCage() {
            Location pos1 = cageCenter.clone().add(3, 0, -3);
            Location pos2 = cageCenter.clone().add(-3, 4, 3);
            Cuboid cuboid = new Cuboid(pos1, pos2);
            for (Block blocks : cuboid) {
                blocks.breakNaturally();
            }
        }


    }
}
