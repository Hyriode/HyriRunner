package fr.hyriode.runner.util;

import fr.hyriode.hyrame.game.util.value.HostValueModifier;
import fr.hyriode.hyrame.game.util.value.ValueProvider;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.utils.player.SavablePlayerInventory;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * Created by AstFaster
 * on 23/08/2022 at 12:58
 */
public class RunnerValues {

    public static final ValueProvider<Boolean> BONUS = new ValueProvider<>(true).addModifiers(new HostValueModifier<>(1, Boolean.class, "bonus"));
    public static final ValueProvider<Boolean> INVINCIBILITY = new ValueProvider<>(false).addModifiers(new HostValueModifier<>(1, Boolean.class, "invincibility"));
    public static final ValueProvider<Boolean> FOOD = new ValueProvider<>(false).addModifiers(new HostValueModifier<>(1, Boolean.class, "food"));
    public static final ValueProvider<Long> GAME_TIME = new ValueProvider<>(10 * 60L).addModifiers(new HostValueModifier<>(1, Long.class, "game-time"));
    public static final ValueProvider<SavablePlayerInventory> INVENTORY = new ValueProvider<>(new SavablePlayerInventory(contents(), armor())).addModifiers(new HostValueModifier<>(1, SavablePlayerInventory.class, "inventory"));

    public static final ValueProvider<Integer> BORDER_INITIAL_SIZE = new ValueProvider<>(1500).addModifiers(new HostValueModifier<>(1, Integer.class, "border-initial-size"));
    public static final ValueProvider<Integer> BORDER_FINAL_SIZE = new ValueProvider<>(50).addModifiers(new HostValueModifier<>(1, Integer.class, "border-final-size"));
    public static final ValueProvider<Double> BORDER_SPEED = new ValueProvider<>(6.0D).addModifiers(new HostValueModifier<>(1, Double.class, "border-speed"));
    public static final ValueProvider<Long> BORDER_TIME = new ValueProvider<>(30L).addModifiers(new HostValueModifier<>(1, Long.class, "border-time"));

    private static ItemStack[] contents() {
        final ItemStack sword = new ItemBuilder(Material.IRON_SWORD).unbreakable().withEnchant(Enchantment.DAMAGE_ALL, 2).build();
        final ItemStack steaks = new ItemBuilder(Material.COOKED_BEEF, 64).build();
        final ItemStack cobWebs = new ItemBuilder(Material.WEB, 1).build();
        final ItemStack gaps = new ItemBuilder(Material.GOLDEN_APPLE, 5).build();
        final ItemStack bucket = new ItemBuilder(Material.WATER_BUCKET).build();
        final ItemStack blocks = new ItemBuilder(Material.STONE, 64).build();
        final ItemStack wood = new ItemBuilder(Material.WOOD, 64).build();
        final ItemStack pick = new ItemBuilder(Material.IRON_PICKAXE).unbreakable().build();
        final ItemStack axe = new ItemBuilder(Material.IRON_AXE).unbreakable().build();

        return new ItemStack[] {sword, blocks, pick, bucket, steaks, cobWebs, wood, blocks, gaps, bucket, axe};
    }

    private static ItemStack[] armor() {
        final ItemStack helmet = new ItemBuilder(Material.IRON_HELMET).unbreakable().withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build();
        final ItemStack chestPlate = new ItemBuilder(Material.DIAMOND_CHESTPLATE).unbreakable().withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build();
        final ItemStack leggings = new ItemBuilder(Material.IRON_LEGGINGS).unbreakable().withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build();
        final ItemStack boots = new ItemBuilder(Material.DIAMOND_BOOTS).unbreakable().withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build();

        return new ItemStack[] {boots, leggings, chestPlate, helmet};
    }

}
