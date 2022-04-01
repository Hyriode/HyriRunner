package fr.hyriode.runner.inventories;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.item.HyriItem;
import fr.hyriode.runner.HyriRunner;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Supplier;

/**
 * Project: HyriRunner
 * Created by Akkashi
 * on 27/03/2022 at 16:03
 */
public class HyriRunnerChooseChallengeItem extends HyriItem<HyriRunner> {

    public HyriRunnerChooseChallengeItem(HyriRunner plugin) {
        super(plugin, "choose_challenge", () -> HyriRunner.getLanguageManager().getMessage("item.challenge"), Material.PAPER);
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        new HyriRunnerChallengeGui(event.getPlayer(), plugin).open();
    }
}