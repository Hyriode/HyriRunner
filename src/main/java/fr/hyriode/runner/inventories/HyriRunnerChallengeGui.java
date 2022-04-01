package fr.hyriode.runner.inventories;

import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.api.settings.HyriLanguage;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.challenges.HyriRunnerChallengeModel;
import fr.hyriode.runner.challenges.HyriRunnerChallenge;
import fr.hyriode.runner.game.HyriRunnerGamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;

public class HyriRunnerChallengeGui extends HyriInventory {

    private static final HyriLanguageMessage name = new HyriLanguageMessage("challenge.gui.name")
            .addValue(HyriLanguage.FR, ChatColor.DARK_AQUA + "Choisissez votre défi")
            .addValue(HyriLanguage.EN, ChatColor.DARK_AQUA + "Choose your challenge");

    private static final HyriLanguageMessage loreChoose = new HyriLanguageMessage("challenge.gui.choose")
            .addValue(HyriLanguage.FR, ChatColor.RED + "Cliquez pour choisir ce défi")
            .addValue(HyriLanguage.EN, ChatColor.RED + "Click to choose your challenge");
    private static final HyriLanguageMessage loreChosen = new HyriLanguageMessage("challenge.gui.chosen")
            .addValue(HyriLanguage.FR, ChatColor.GREEN + "Vous avez choisi ce défi")
            .addValue(HyriLanguage.EN, ChatColor.GREEN + "You have chosen this challenge");
    private static final HyriLanguageMessage challengeSelected = new HyriLanguageMessage("message.challenge-selected")
            .addValue(HyriLanguage.FR, ChatColor.DARK_AQUA + "Vous avez choisi le défi: %challenge%")
            .addValue(HyriLanguage.EN, ChatColor.DARK_AQUA + "You choose the challenge: %challenge%");
    private static final HyriLanguageMessage challengeUnselected = new HyriLanguageMessage("message.challenge-unselected")
            .addValue(HyriLanguage.FR, ChatColor.DARK_AQUA + "Vous ne choisissez aucun défi.")
            .addValue(HyriLanguage.EN, ChatColor.DARK_AQUA + "You're choosing no challenge.");

    public HyriRunnerChallengeGui(Player owner, HyriRunner plugin) {
        super(owner, name.getForPlayer(owner), 27);
        HyriRunnerGamePlayer player = plugin.getGame().getPlayer(owner.getUniqueId());

        for (HyriRunnerChallengeModel value : HyriRunnerChallengeModel.values()) {
            Optional<HyriRunnerChallenge> oChallenge = HyriRunnerChallenge.getWithModel(value);
            oChallenge.ifPresent(challenge -> {
                if (player.getChallenge() == null || !player.getAccount().getLastSelectedChallenge().equals(challenge.getModel())) {
                    this.addItem(new ItemBuilder(challenge.getIcon())
                                    .withName(HyriRunner.getLanguageManager().getMessage(challenge.getKey()).getForPlayer(owner))
                                    .withLore(
                                            HyriRunner.getLanguageManager().getMessage(challenge.getLore()[0]).getForPlayer(owner),
                                            HyriRunner.getLanguageManager().getMessage(challenge.getLore()[1]).getForPlayer(owner),
                                            ChatColor.GRAY + " ",
                                            loreChoose.getForPlayer(owner)
                                    )
                                    .withAllItemFlags()
                                    .build()
                            , event -> {
                                event.setCancelled(true);
                                player.setChallenge(challenge);
                                player.getAccount().setLastSelectedChallenge(challenge.getModel());
                                player.sendMessage(challengeSelected.getForPlayer(player.getPlayer()).replace("%challenge%", HyriRunner.getLanguageManager().getMessage(challenge.getKey()).getForPlayer(player.getPlayer())));
                                event.getWhoClicked().closeInventory();
                            });
                } else {
                    this.addItem(new ItemBuilder(challenge.getIcon())
                                    .withName(HyriRunner.getLanguageManager().getMessage(challenge.getKey()).getForPlayer(owner))
                                    .withLore(
                                            HyriRunner.getLanguageManager().getMessage(challenge.getLore()[0]).getForPlayer(owner),
                                            HyriRunner.getLanguageManager().getMessage(challenge.getLore()[1]).getForPlayer(owner),
                                            ChatColor.GRAY + " ",
                                            loreChosen.getForPlayer(owner)
                                    )
                                    .withAllItemFlags()
                                    .withGlow()
                                    .build()
                            , event -> {
                                event.setCancelled(true);
                                player.setChallenge(null);
                                player.getAccount().setLastSelectedChallenge(null);
                                player.sendMessage(challengeUnselected.getForPlayer(player.getPlayer()));
                                event.getWhoClicked().closeInventory();
                            });
                }
            });
        }
    }


}
