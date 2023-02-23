package fr.hyriode.runner.bonus;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.function.Consumer;

/**
 * Created by AstFaster
 * on 21/08/2022 at 15:49
 */
public enum RunnerBonus {

    NOTHING("nothing", Material.BARRIER, 40, player -> {}),
    SWORD("sword", Material.DIAMOND_SWORD, 12, player -> {
        final PlayerInventory inventory = player.getInventory();;

        inventory.remove(Material.IRON_SWORD);
        inventory.addItem(new ItemBuilder(Material.DIAMOND_SWORD).withEnchant(Enchantment.DAMAGE_ALL, 2).unbreakable().build());
    }),
    ARMOR("armor", Material.DIAMOND_CHESTPLATE, 13, player -> {
        final PlayerInventory inventory = player.getInventory();

        inventory.setHelmet(new ItemBuilder(Material.DIAMOND_HELMET).unbreakable().withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
        inventory.setLeggings(new ItemBuilder(Material.DIAMOND_LEGGINGS).unbreakable().build());
        inventory.setChestplate(new ItemBuilder(Material.DIAMOND_CHESTPLATE).unbreakable().build());
        inventory.setBoots(new ItemBuilder(Material.DIAMOND_BOOTS).withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).unbreakable().build());
    }),
    DAMAGE_POTIONS("damage-potions", new Potion(PotionType.INSTANT_DAMAGE).toItemStack(1), 14, player -> {
        final PlayerInventory inventory = player.getInventory();
        final ItemStack potion = new Potion(PotionType.INSTANT_DAMAGE).splash().toItemStack(3);

        inventory.addItem(potion);
    }),
    HEAL_POTIONS("heal-potions", new Potion(PotionType.INSTANT_HEAL).toItemStack(1), 21, player -> {
        final PlayerInventory inventory = player.getInventory();
        final ItemStack potion = new Potion(PotionType.INSTANT_HEAL).splash().toItemStack(3);

        inventory.addItem(potion);
    }),
    LAVA("lava", Material.LAVA_BUCKET, 22, player -> player.getInventory().addItem(new ItemStack(Material.LAVA_BUCKET))),
    ROD("rod", Material.FISHING_ROD, 23, player -> player.getInventory().addItem(new ItemBuilder(Material.FISHING_ROD).withEnchant(Enchantment.DURABILITY, 1).build()))

    ;

    private HyriLanguageMessage displayName;

    private final String id;
    private final ItemStack icon;
    private final int slot;
    private final Consumer<Player> action;

    RunnerBonus(String id, ItemStack icon, int slot, Consumer<Player> action) {
        this.id = id;
        this.icon = icon;
        this.slot = slot;
        this.action = action;
    }

    RunnerBonus(String id, Material icon, int slot, Consumer<Player> action) {
        this(id, new ItemStack(icon), slot, action);
    }
    public String getId() {
        return this.id;
    }

    public ItemStack getIcon() {
        return this.icon.clone();
    }

    public int getSlot() {
        return this.slot;
    }

    public void trigger(Player player) {
        this.action.accept(player);
    }

    public HyriLanguageMessage getDisplayName() {
        return this.displayName == null ? this.displayName = HyriLanguageMessage.get("bonus." + this.id + ".name") : this.displayName;
    }

}
