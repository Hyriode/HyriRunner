package fr.hyriode.runner.inventories;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.item.HyriItem;
import fr.hyriode.runner.HyriRunner;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Project: HyriRunner
 * Created by Akkashi
 * on 27/03/2022 at 16:03
 */
public class RunnerChooseChallengeItem extends HyriItem<HyriRunner> {

    public RunnerChooseChallengeItem(HyriRunner plugin) {
        super(plugin, "choose_challenge", () -> plugin.getLanguageManager().getMessage("item.challenge"), Material.PAPER);
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        new RunnerChallengeGui(event.getPlayer(), plugin).open();
    }
}