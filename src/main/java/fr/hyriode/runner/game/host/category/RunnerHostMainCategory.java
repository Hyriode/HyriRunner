package fr.hyriode.runner.game.host.category;

import fr.hyriode.hyrame.host.HostCategory;
import fr.hyriode.hyrame.host.category.HostBorderCategory;
import fr.hyriode.hyrame.host.option.BooleanOption;
import fr.hyriode.hyrame.host.option.InventoryOption;
import fr.hyriode.hyrame.host.option.TimeOption;
import fr.hyriode.hyrame.utils.UsefulDisplay;
import fr.hyriode.runner.util.RunnerValues;
import org.bukkit.Material;

/**
 * Created by AstFaster
 * on 23/08/2022 at 12:51
 */
public class RunnerHostMainCategory extends HostCategory {

    public RunnerHostMainCategory() {
        super(UsefulDisplay.categoryDisplay("runner-main", Material.DIAMOND_BOOTS));

        this.addOption(21, new BooleanOption(UsefulDisplay.optionDisplay("bonus", Material.GOLD_BLOCK), true));
        this.addOption(22, new BooleanOption(UsefulDisplay.optionDisplay("invincibility", Material.ANVIL), false));
        this.addOption(23, new BooleanOption(UsefulDisplay.optionDisplay("food", Material.COOKED_BEEF), false));
        this.addOption(30, new TimeOption(UsefulDisplay.optionDisplay("game-time", Material.WATCH), 10 * 60L, 0L, Long.MAX_VALUE, new long[] {60L, 5 * 60L}));
        this.addOption(31, new InventoryOption(UsefulDisplay.optionDisplay("inventory", Material.CHEST), RunnerValues.INVENTORY.getDefaultValue()));

        this.addSubCategory(32, new HostBorderCategory(UsefulDisplay.categoryDisplay("runner-border", Material.BARRIER))
                .addInitialSizeOption("border-initial-size",RunnerValues.BORDER_INITIAL_SIZE.getDefaultValue(), 100, 10000)
                .addFinalSizeOption("border-final-size", RunnerValues.BORDER_FINAL_SIZE.getDefaultValue(), 10, 10000)
                .addSpeedOption("border-speed", RunnerValues.BORDER_SPEED.getDefaultValue(), 1, 100)
                .addTimeOption("border-time", 0L, 0L, Long.MAX_VALUE));
    }

}
