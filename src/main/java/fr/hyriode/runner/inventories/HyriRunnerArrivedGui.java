package fr.hyriode.runner.inventories;

import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.hyriapi.settings.HyriLanguage;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.game.HyriRunnerMessages;
import fr.hyriode.runner.listeners.HyriRunnerGameListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class HyriRunnerArrivedGui extends HyriInventory {

    private final HyriRunner plugin;

    HyriLanguageMessage swordName = new HyriLanguageMessage("arrived.gui.sword")
            .addValue(HyriLanguage.FR, ChatColor.AQUA + "Amélioration d'épée")
            .addValue(HyriLanguage.EN, ChatColor.AQUA + "Sword upgrade");
    HyriLanguageMessage armorName = new HyriLanguageMessage("arrived.gui.armor")
            .addValue(HyriLanguage.FR, ChatColor.AQUA + "Amélioration d'armure")
            .addValue(HyriLanguage.EN, ChatColor.AQUA + "Armor upgrade");
    HyriLanguageMessage nothingName = new HyriLanguageMessage("arrived.gui.nothing")
            .addValue(HyriLanguage.FR, ChatColor.RED + "Aucune amélioration")
            .addValue(HyriLanguage.EN, ChatColor.RED + "No upgrade");
    ItemBuilder sword = new ItemBuilder(Material.DIAMOND_SWORD).withName(swordName.getForPlayer(getOwner().getPlayer()));
    ItemBuilder armor = new ItemBuilder(Material.DIAMOND_CHESTPLATE).withName(armorName.getForPlayer(getOwner().getPlayer()));
    ItemBuilder nothing = new ItemBuilder(Material.BARRIER).withName(nothingName.getForPlayer(getOwner().getPlayer()));

    public HyriRunnerArrivedGui(Player owner, String name, HyriRunner plugin) {
        super(owner, name, 9);
        this.plugin = plugin;
        this.setItem(1, sword.build(), event -> {
            event.setCancelled(true);
            Player p = (Player) event.getWhoClicked();
            HyriRunnerGameListener.hasChosen = true;
            p.closeInventory();
            p.getInventory().remove(Material.IRON_SWORD);
            p.getInventory().addItem(new ItemBuilder(Material.DIAMOND_SWORD).withEnchant(Enchantment.DAMAGE_ALL, 2).unbreakable().build());
            plugin.getGame().sendMessageToAll(player -> HyriRunnerMessages.FIRST_PLACE_SWORD.get().getForPlayer(player)
                    .replace("%player%", plugin.getGame().getPlayer(owner.getUniqueId()).getTeam().getColor().getChatColor() + owner.getName()));
        });
        this.setItem(4, nothing.build(), event -> {
            event.setCancelled(true);
            Player p = (Player) event.getWhoClicked();
            HyriRunnerGameListener.hasChosen = true;
            plugin.getGame().getPlayer(p.getUniqueId()).setWarrior(true);
            p.closeInventory();
        });
        this.setItem(7, armor.build(), event -> {
            event.setCancelled(true);
            Player p = (Player) event.getWhoClicked();
            HyriRunnerGameListener.hasChosen = true;
            p.closeInventory();
            p.getEquipment().setHelmet(new ItemBuilder(Material.DIAMOND_HELMET).unbreakable().build());
            p.getEquipment().setLeggings(new ItemBuilder(Material.DIAMOND_LEGGINGS).unbreakable().build());
            p.getEquipment().setChestplate(new ItemBuilder(Material.DIAMOND_CHESTPLATE).unbreakable().build());
            p.getEquipment().setBoots(new ItemBuilder(Material.DIAMOND_BOOTS).unbreakable().build());
            plugin.getGame().sendMessageToAll(player -> HyriRunnerMessages.FIRST_PLACE_ARMOR.get().getForPlayer(player)
                    .replace("%player%", plugin.getGame().getPlayer(owner.getUniqueId()).getTeam().getColor().getChatColor() + owner.getName()));
        });
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        super.onClose(event);

        Player p = (Player) event.getPlayer();

        if (!HyriRunnerGameListener.hasChosen) {
            this.open();
        }
        Title.sendTitle(
                p,
                HyriRunnerMessages.ARRIVED_TITLE.get().getForPlayer(p),
                HyriRunnerMessages.ARRIVED_SUB.get().getForPlayer(p)
                        .replace("%position%", String.valueOf(plugin.getGame().getPlayer(p.getUniqueId()).getPosition())),
                1,
                4 * 20,
                1
        );
    }
}
