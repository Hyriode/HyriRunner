package fr.hyriode.runner.challenge.gui;

import fr.hyriode.hyrame.inventory.pagination.PaginatedInventory;
import fr.hyriode.hyrame.inventory.pagination.PaginatedItem;
import fr.hyriode.hyrame.inventory.pagination.PaginationArea;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.utils.Pagination;
import fr.hyriode.hyrame.utils.Symbols;
import fr.hyriode.hyrame.utils.list.ListReplacer;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.RunnerChallengeModel;
import fr.hyriode.runner.api.RunnerPlayer;
import fr.hyriode.runner.challenge.RunnerChallenge;
import fr.hyriode.runner.challenge.RunnerChallengeDifficulty;
import fr.hyriode.runner.game.RunnerGamePlayer;
import fr.hyriode.runner.util.RunnerHead;
import fr.hyriode.runner.util.RunnerMessage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RunnerChallengeGUI extends PaginatedInventory {

    private boolean favorites;

    private RunnerChallengeDifficulty difficulty;

    private final HyriRunner plugin;

    public RunnerChallengeGUI(Player owner, boolean favorites, RunnerChallengeDifficulty difficulty, HyriRunner plugin) {
        super(owner, name(owner, plugin), 6 * 9);
        this.favorites = favorites;
        this.difficulty = difficulty;
        this.plugin = plugin;
        this.paginationManager.setArea(new PaginationArea(18, 44));

        this.setHorizontalLine(9, 17, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());
        this.setHorizontalLine(45, 53, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());

        this.setItem(49, new ItemBuilder(Material.BARRIER)
                .withName(RunnerMessage.CHALLENGE_NONE_ITEM_NAME.asString(this.owner))
                .build(),
                event -> {
                    final RunnerGamePlayer gamePlayer = this.plugin.getGame().getPlayer(this.owner);

                    gamePlayer.setChallenge(null);

                    new RunnerChallengeGUI(this.owner, this.favorites, this.difficulty, this.plugin).open();
                });

        this.addCategoryItems();
        this.addChallenges();
    }

    private static String name(Player player, HyriRunner plugin) {
        final RunnerGamePlayer gamePlayer = plugin.getGame().getPlayer(player);
        final RunnerChallenge challenge = gamePlayer.getChallenge();

        return RunnerMessage.CHALLENGE_GUI_NAME.asString(player).replace("%challenge%", challenge == null ? ChatColor.RED + Symbols.CROSS_STYLIZED_BOLD : challenge.getName(player));
    }

    private void addChallenges() {
        final Pagination<PaginatedItem> pagination = this.paginationManager.getPagination();
        final List<RunnerChallenge> challenges = this.favorites ? RunnerChallenge.getChallenges() : RunnerChallenge.getChallenges(this.difficulty);
        final RunnerGamePlayer gamePlayer = this.plugin.getGame().getPlayer(this.owner);
        final RunnerPlayer account = gamePlayer.getAccount();
        final RunnerChallenge playerChallenge = gamePlayer.getChallenge();

        pagination.clear();

        for (RunnerChallenge challenge : challenges) {
            final RunnerChallengeModel challengeModel = challenge.getModel();
            final boolean selected = playerChallenge != null && playerChallenge.getModel() == challengeModel;
            final boolean favorites = account.getFavoritesChallenges().contains(challengeModel);

            if (this.favorites && !favorites) {
                continue;
            }

            pagination.add(PaginatedItem.from(this.createChallengeItem(challenge, selected, favorites, account.getCompletedChallenges().contains(challengeModel)), event -> {
                if (event.isLeftClick()) {
                    if (gamePlayer.getChallenge().getModel() == challengeModel) {
                        return;
                    }

                    gamePlayer.setChallenge(challenge);

                    this.owner.playSound(this.owner.getLocation(), Sound.SUCCESSFUL_HIT, 0.5F, 1.3F);

                    new RunnerChallengeGUI(this.owner, this.favorites, this.difficulty, this.plugin).open();
                } else if (event.isRightClick()){
                    if (account.addFavoriteChallenge(challengeModel)) {
                        this.owner.playSound(this.owner.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.7F);

                        this.addChallenges();
                    } else {
                        this.owner.playSound(this.owner.getLocation(), Sound.FIZZ, 0.5F, 1.0F);

                        account.removeFavoriteChallenge(challengeModel);

                        this.addChallenges();
                    }
                }
            }));
        }

        this.paginationManager.updateGUI();
    }

    private void addCategoryItems() {
        this.setItem(0, new ItemBuilder(Material.NETHER_STAR)
                .withName(RunnerMessage.CHALLENGE_FAVORITES_ITEM_NAME.asString(this.owner))
                .build(),
                event -> {
                    this.difficulty = null;
                    this.favorites = true;

                    this.addCategoryItems();
                    this.addChallenges();

                    this.owner.playSound(this.owner.getLocation(), Sound.CLICK, 0.5F, 2.0F);
                });

        this.setItem(8, ItemBuilder.asHead(RunnerHead.DICE)
                .withName(RunnerMessage.CHALLENGE_RANDOM_ITEM_NAME.asString(this.owner))
                .build(),
                event -> {
                    final List<RunnerChallenge> challenges = RunnerChallenge.getChallenges();
                    final RunnerChallenge randomChallenge = challenges.get(ThreadLocalRandom.current().nextInt(challenges.size()));
                    final RunnerGamePlayer gamePlayer = this.plugin.getGame().getPlayer(this.owner);

                    gamePlayer.setChallenge(randomChallenge);

                    this.owner.playSound(this.owner.getLocation(), Sound.FIREWORK_BLAST, 1.0F, 2.0F);

                    new RunnerChallengeGUI(this.owner, this.favorites, this.difficulty, this.plugin).open();
                });

        this.addDifficultyItems();
    }

    @SuppressWarnings("deprecation")
    private void addDifficultyItems() {
        int slot = 2;
        for (RunnerChallengeDifficulty difficulty : RunnerChallengeDifficulty.values()) {
            final ItemBuilder builder = new ItemBuilder(Material.INK_SACK, 1, difficulty.getColor().getDyeData())
                    .withName(RunnerMessage.CHALLENGE_DIFFICULTY_ITEM_NAME.asString(this.owner).replace("%difficulty%", difficulty.asString()));

            if (this.difficulty == difficulty) {
                builder.withGlow();
            }

            this.setItem(slot, builder.build(), event -> {
                if (this.difficulty == difficulty) {
                    return;
                }

                this.owner.playSound(this.owner.getLocation(), Sound.CLICK, 0.5F, 2.0F);

                new RunnerChallengeGUI(this.owner, false, difficulty, this.plugin).open();
            });
            slot++;
        }

    }

    private ItemStack createChallengeItem(RunnerChallenge challenge, boolean selected, boolean favorite, boolean completed) {
        final ItemBuilder builder = new ItemBuilder(challenge.getIcon()).withAllItemFlags();
        final List<String> lore = ListReplacer.replace(RunnerMessage.CHALLENGE_ITEM_LORE.asList(this.owner), "%difficulty%", challenge.getDifficulty().asString())
                .replace("%completed%", completed ? ChatColor.GREEN + Symbols.TICK_BOLD : ChatColor.RED + Symbols.CROSS_STYLIZED_BOLD)
                .replace("%status_line%", (selected ? RunnerMessage.CHALLENGE_SELECTED_LINE : RunnerMessage.CHALLENGE_SELECT_LINE).asString(this.owner))
                .replace("%favorite_line%", (favorite ? RunnerMessage.CHALLENGE_REMOVE_FAVORITE_LINE : RunnerMessage.CHALLENGE_ADD_FAVORITE_LINE).asString(this.owner))
                .list();
        final List<String> challengeLore = challenge.getLore(this.owner);

        lore.addAll(0, challengeLore);
        lore.add(challengeLore.size(), "");

        if (selected) {
            builder.withGlow();
        }

        return builder.withName((favorite ? ChatColor.GOLD + Symbols.STAR + " " : "") + ChatColor.AQUA + challenge.getName(this.owner))
                .withLore(lore)
                .build();
    }

    @Override
    public void updatePagination(int page, List<PaginatedItem> items) {
        this.addDefaultPagesItems(45, 53);
    }

}
