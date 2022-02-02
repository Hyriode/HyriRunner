package fr.hyriode.runner.game.gamemap;

import net.minecraft.server.v1_8_R3.BiomeBase;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import java.lang.reflect.Field;

public class HyriRunnerGameMap {

    private final String name;

    public HyriRunnerGameMap(String name) {
        this.name = name;
    }

    public void create() {
        WorldCreator worldCreator = new WorldCreator(name);
        worldCreator.type(WorldType.NORMAL);
        worldCreator.environment(World.Environment.NORMAL);
        worldCreator.createWorld();
    }

    public void patch() {
        try {
            Field biomesField = BiomeBase.class.getDeclaredField("biomes");
            biomesField.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(biomesField, biomesField.getModifiers() & 0xFFFFFFEF);
            if (biomesField.get((Object)null) instanceof BiomeBase[]) {
                BiomeBase[] biomes = (BiomeBase[])biomesField.get((Object)null);
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
                biomesField.set((Object)null, biomes);
            }
        } catch (NoSuchFieldException|IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }


}
