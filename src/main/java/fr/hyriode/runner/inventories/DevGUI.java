package fr.hyriode.runner.inventories;

import fr.hyriode.dev.HyriDev;
import fr.hyriode.dev.gui.proxy.ProxiesGUI;
import fr.hyriode.dev.gui.server.ServersGUI;
import fr.hyriode.dev.util.Head;
import fr.hyriode.hyrame.inventory.pagination.PaginatedInventory;
import fr.hyriode.hyrame.inventory.pagination.PaginatedItem;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.utils.Symbols;
import fr.hyriode.runner.HyriRunner;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Project: Hyriode
 * Created by AstFaster
 * on 20/05/2022 at 21:27
 */
public abstract class RTFGui extends PaginatedInventory {

    public static class Manager {

        private static Manager instance;

        private final List<Category> categories;

        public Manager() {
            instance = this;
            this.categories = new ArrayList<>();


        }

        public void registerCategory(Category category) {
            this.categories.add(category);
        }

        public List<Category> getCategories() {
            return this.categories;
        }

        static Manager get() {
            return instance;
        }

    }

    public static class Category {

        private final ItemStack item;

        private final String name;
        private final List<String> lore;
        private final int slot;

        private final Class<? extends RTFGui> guiClass;

        public Category(ItemStack item, String name, List<String> lore, int slot, Class<? extends RTFGui> guiClass) {
            if (slot >= 9) {
                throw new IllegalArgumentException("Slot must be less than 9!");
            }
            this.item = item;
            this.name = name;
            this.lore = new ArrayList<>();
            this.slot = slot;
            this.guiClass = guiClass;

            for (String line : lore) {
                this.lore.add(ChatColor.GRAY + line);
            }
        }

        public ItemStack getItem() {
            return this.item;
        }

        public String getName() {
            return this.name;
        }

        public List<String> getLore() {
            return this.lore;
        }

        public int getSlot() {
            return this.slot;
        }

        public Class<? extends RTFGui> getGuiClass() {
            return this.guiClass;
        }

    }

    protected boolean usingPages;

    protected final HyriRunner plugin;

    public RTFGui(Player owner, String name, HyriDev plugin) {
        super(owner, ChatColor.DARK_AQUA + "Dev " + ChatColor.DARK_GRAY + "> " + ChatColor.GRAY + name, 6 * 9);
        this.plugin = plugin;

        this.setHorizontalLine(0, 8, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());
        this.setHorizontalLine(45, 53, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());

        for (Category category : Manager.get().getCategories()) {
            this.setItem(category.getSlot(), new ItemBuilder(category.getItem())
                    .withName(ChatColor.DARK_AQUA + category.getName())
                    .withLore(category.getLore())
                    .build(), event -> this.openSubGUI(category.getGuiClass()));
        }
    }

    protected void addPagesItems() {
        if (!this.usingPages) {
            return;
        }

        this.setItem(45, new ItemBuilder(Material.ARROW)
                .withName(ChatColor.DARK_AQUA + "Page précédente " + ChatColor.DARK_GRAY + Symbols.LINE_VERTICAL_BOLD + ChatColor.GRAY + " " + (this.paginationManager.currentPage() + 1) + "/" + this.paginationManager.getPagination().totalPages())
                .withLore(ChatColor.GRAY + "Passe à la page précédente")
                .build(), event -> this.paginationManager.previousPage());

        this.setItem(53, new ItemBuilder(Material.ARROW)
                .withName(ChatColor.DARK_AQUA + "Page suivante " + ChatColor.DARK_GRAY + Symbols.LINE_VERTICAL_BOLD + ChatColor.GRAY + " " + (this.paginationManager.currentPage() + 1) + "/" + this.paginationManager.getPagination().totalPages())
                .withLore(ChatColor.GRAY + "Passe à la page suivante")
                .build(), event -> this.paginationManager.nextPage());
    }

    private void openSubGUI(Class<? extends RTFGui> guiClass) {
        if (this.getClass() == guiClass) {
            return;
        }

        try {
            final Constructor<? extends RTFGui> constructor = guiClass.getDeclaredConstructor(Player.class, HyriRunner.class);
            final RTFGui gui = constructor.newInstance(this.owner, this.plugin);

            gui.open();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updatePagination(int page, List<PaginatedItem> items) {
        this.addPagesItems();
    }

}