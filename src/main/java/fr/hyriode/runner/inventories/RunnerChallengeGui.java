package fr.hyriode.runner.inventories;

import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.api.settings.HyriLanguage;
import fr.hyriode.hyrame.utils.Symbols;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.challenges.HyriRunnerChallengeModel;
import fr.hyriode.runner.challenges.RunnerChallenge;
import fr.hyriode.runner.challenges.RunnerChallengeDifficulty;
import fr.hyriode.runner.game.RunnerGamePlayer;
import fr.hyriode.runner.game.RunnerMessage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RunnerChallengeGui extends HyriInventory {

    public RunnerChallengeGui(Player owner, HyriRunner plugin) {
        super(owner, plugin.getLanguageManager().getValue(owner, "gui.challenge.name"), 54);
    }

    private void addCategoryItems() {
        final String headTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzg4MWNjMjc0N2JhNzJjYmNiMDZjM2NjMzMxNzQyY2Q5ZGUyNzFhNWJiZmZkMGVjYjE0ZjFjNmE4YjY5YmM5ZSJ9fX0=";
        final ItemStack random = ItemBuilder.asHead()
                .withHeadTexture(headTexture)
                // ADD NAME AND LORE
                .build();
        final ItemStack favorites = new ItemBuilder(Material.NETHER_STAR)
                // ADD NAME AND LORE
                .build();

        this.addDifficultyItems();
    }

    private void addDifficultyItems() {
        for (int i = 2; i <= 6; i++) {
            for (RunnerChallengeDifficulty value : RunnerChallengeDifficulty.values()) {
                this.setItem(
                        i,
                        new ItemBuilder(Material.INK_SACK, 1, value.getColor().getDyeData())
                                .withName(RunnerMessage.GUI_CHALLENGE_DIFFICULTY + value.getAsString(this.owner))
                                //TODO: Add lore
                                .build()
                );
                break;
            }
        }
    }

    private ItemStack getChallengeItem(final RunnerChallenge challenge, final boolean selected) {
        final ItemBuilder itemBuilder = new ItemBuilder(challenge.getIcon()).withAllItemFlags();
        final List<String> lore = new ArrayList<>();

        lore.add(RunnerMessage.GUI_CHALLENGE_DIFFICULTY.asString(this.owner) + challenge.getDifficulty().getAsString(this.owner));
        lore.add(" ");
        lore.addAll(2, challenge.getLore(this.owner));
        lore.add(" ");

        if(selected) {
            lore.add(RunnerMessage.GUI_CHALLENGE_SELECTED.asString(this.owner));
            itemBuilder.withGlow();
        } else {
            lore.add(RunnerMessage.GUI_CHALLENGE_SELECT.asString(this.owner));
        }

        return itemBuilder
                .withName(challenge.getName(this.owner))
                .withLore(lore)
                .build();
    }


    public static enum RunnerChallengeGuiCategories{

    }
}
