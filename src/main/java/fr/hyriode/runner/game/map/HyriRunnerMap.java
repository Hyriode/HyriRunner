package fr.hyriode.runner.game.map;

import net.minecraft.server.v1_8_R3.BiomeBase;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class HyriRunnerMap {

    private final String name;

    public HyriRunnerMap(String name) {
        this.name = name;

        this.create();
        this.patch();
    }

    private void create() {
        WorldCreator worldCreator = new WorldCreator(name);
        worldCreator.type(WorldType.NORMAL);
        worldCreator.environment(World.Environment.NORMAL);
        worldCreator.createWorld();
    }

    private void patch() {
        try {
            Field biomesField = BiomeBase.class.getDeclaredField("biomes");
            biomesField.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(biomesField, biomesField.getModifiers() & 0xFFFFFFEF);
            if (biomesField.get(null) instanceof BiomeBase[]) {
                BiomeBase[] biomes = (BiomeBase[])biomesField.get(null);
                biomes[BiomeBase.DEEP_OCEAN.id] = BiomeBase.FOREST;
                biomes[BiomeBase.FROZEN_OCEAN.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.FROZEN_OCEAN.id] = BiomeBase.FOREST;
                biomes[BiomeBase.OCEAN.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.COLD_BEACH.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.BEACH.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.STONE_BEACH.id] = BiomeBase.FOREST;
                biomes[BiomeBase.JUNGLE.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.JUNGLE_EDGE.id] = BiomeBase.FOREST;
                biomes[BiomeBase.JUNGLE_HILLS.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.SWAMPLAND.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.EXTREME_HILLS_PLUS.id] = BiomeBase.PLAINS;
                biomesField.set(null, biomes);
            }
        } catch (NoSuchFieldException|IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public List<HyriRunnerMapChunk> getChunksAround(Chunk origin, int radius) {
        final int length = (radius * 2) + 1;
        final List<HyriRunnerMapChunk> chunks = new ArrayList<>(length * length);

        final int cX = origin.getX();
        final int cZ = origin.getZ();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                chunks.add(new HyriRunnerMapChunk(cX + x, cZ + z));
            }
        }
        return chunks;
    }

    public String getName() {
        return name;
    }

}
