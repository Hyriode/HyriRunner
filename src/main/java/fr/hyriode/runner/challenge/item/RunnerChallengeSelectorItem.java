package fr.hyriode.runner.challenge.item;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.item.HyriItem;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.challenge.RunnerChallengeDifficulty;
import fr.hyriode.runner.challenge.gui.RunnerChallengeGUI;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Project: HyriRunner
 * Created by Akkashi
 * on 27/03/2022 at 16:03
 */
public class RunnerChallengeSelectorItem extends HyriItem<HyriRunner> {

    public RunnerChallengeSelectorItem(HyriRunner plugin) {
        super(plugin, "challenge_selector", () -> HyriLanguageMessage.get("item.challenge"), null, Material.PAPER);
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        new RunnerChallengeGUI(event.getPlayer(), false, RunnerChallengeDifficulty.EASY, this.plugin).open();
    }

}