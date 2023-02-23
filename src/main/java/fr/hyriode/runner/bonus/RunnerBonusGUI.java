package fr.hyriode.runner.bonus;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.utils.BroadcastUtil;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.game.RunnerGame;
import fr.hyriode.runner.util.RunnerMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class RunnerBonusGUI extends HyriInventory {

    private boolean chosen;

    public RunnerBonusGUI(Player owner, HyriRunner plugin) {
        super(owner, name(owner, "gui.bonus.name"), 6 * 9);

        for (RunnerBonus bonus : RunnerBonus.values()) {
            final ItemStack itemStack = new ItemBuilder(bonus.getIcon())
                    .withName(ChatColor.AQUA + bonus.getDisplayName().getValue(this.owner))
                    .withAllItemFlags()
                    .build();

            this.setItem(bonus.getSlot(), itemStack, event -> {
                this.chosen = true;
                this.owner.closeInventory();

                bonus.trigger(this.owner);

                final RunnerGame game = plugin.getGame();

                HyriAPI.get().getEventBus().publish(new RunnerBonusSelectedEvent(game, game.getPlayer(this.owner), bonus));

                if (bonus == RunnerBonus.NOTHING) {
                    return;
                }

                BroadcastUtil.broadcast(player -> RunnerMessage.BONUS_SELECTED.asString(player)
                        .replace("%player%", game.getPlayer(this.owner).formatNameWithTeam())
                        .replace("%bonus%", bonus.getDisplayName().getValue(player)));
            });
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        if (!this.chosen) {
            this.open();
        }
    }

}
