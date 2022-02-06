package fr.hyriode.runner.game;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.util.HyriGameItems;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.challenges.HyriRunnerChallengeModel;
import fr.hyriode.runner.api.player.HyriRunnerPlayer;
import fr.hyriode.runner.api.statistics.HyriRunnerStatistics;
import fr.hyriode.runner.challenges.HyriRunnerChallenge;
import fr.hyriode.runner.game.map.HyriRunnerSafeTeleport;
import fr.hyriode.runner.game.scoreboard.HyriRunnerFirstPhaseScoreboard;
import fr.hyriode.runner.game.scoreboard.HyriRunnerScoreboard;
import fr.hyriode.runner.game.team.HyriRunnerGameTeam;
import fr.hyriode.runner.game.team.HyriRunnerGameTeams;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class HyriRunnerGame extends HyriGame<HyriRunnerGamePlayer> {

    private WorldBorder wb;
    private boolean accessible;
    private boolean pvp;
    private boolean damage;
    private boolean canPlace = false;
    private boolean borderEnd = false;

    private HyriRunnerGameTask gameTask;
    private HyriRunnerArrow arrow;

    private final HyriRunner plugin;

    public HyriRunnerGame(IHyrame hyrame, HyriRunner plugin) {
        super(hyrame, plugin, "therunner", "TheRunner", HyriRunnerGamePlayer.class);
        this.plugin = plugin;
        this.maxPlayers = HyriRunnerGameType.getByName(plugin.getConfiguration().getGameType()).orElse(HyriRunnerGameType.SOLO).getTeamSize() * 12;
        this.minPlayers = this.maxPlayers / 3;

        this.damage = false;
        this.pvp = false;
        this.accessible = false;

        this.registerTeams();
    }

    private void registerTeams() {
        for (HyriRunnerGameTeams value : HyriRunnerGameTeams.values()) {
            this.registerTeam(new HyriRunnerGameTeam(plugin, value));
        }
    }

    @Override
    public void handleLogin(Player player) {
        super.handleLogin(player);
        player.getInventory().setArmorContents(null);
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.setFoodLevel(20);
        player.setHealth(20);
        player.setLevel(0);
        player.setExp(0.0F);
        player.setCanPickupItems(false);

        final UUID uuid = player.getUniqueId();
        final HyriRunnerGamePlayer gamePlayer = this.getPlayer(uuid);

        gamePlayer.setPlugin(this.plugin);

        HyriRunnerPlayer account = this.plugin.getApi().getPlayerManager().getPlayer(uuid);

        if (account == null) {
            account = new HyriRunnerPlayer(uuid);
        }

        gamePlayer.setAccount(account);
        gamePlayer.setConnectionTime();
        Optional<HyriRunnerChallenge> challenge = HyriRunnerChallenge.getWithModel(account.getLastSelectedChallenge());
        challenge.ifPresent(hyriRunnerChallenge -> {
            gamePlayer.setChallenge(hyriRunnerChallenge);
            gamePlayer.sendMessage(HyriRunnerMessages.LAST_CHALLENGE_USED.get().getForPlayer(player)
                    .replace("%challenge%", HyriRunner.getLanguageManager().getMessage(gamePlayer.getChallenge().getKey()).getForPlayer(player)));
        });

        HyriGameItems.TEAM_CHOOSER.give(this.hyrame, player, 0);
        player.getInventory().setItem(4, new ItemBuilder(Material.PAPER)
                .withName(HyriRunner.getLanguageManager().getMessage("item.challenge")
                        .getForPlayer(player))
                .build());
        HyriGameItems.LEAVE_ITEM.give(this.hyrame, player, 8);

        player.teleport(plugin.getConfiguration().getSpawn());
    }

    @Override
    public void handleLogout(Player player) {
        final UUID uuid = player.getUniqueId();
        final HyriRunnerGamePlayer gamePlayer = this.getPlayer(uuid);
        final HyriRunnerPlayer account = gamePlayer.getAccount();
        final HyriRunnerStatistics statistics = account.getStatistics();

        if (this.state != HyriGameState.READY && this.state != HyriGameState.WAITING) {
            gamePlayer.getScoreboard().hide();

            statistics.setPlayedTime(gamePlayer.getPlayedTime());
            statistics.addGamesPlayed(1);
            statistics.addKills(gamePlayer.getKills());
            statistics.addDeaths(gamePlayer.getDeaths());

            account.getCompletedChallenges().add(gamePlayer.getChallenge().getModel());
        }
        if(gamePlayer.getChallenge() != null) {
            account.setLastSelectedChallenge(gamePlayer.getChallenge().getModel());
        }
        this.plugin.getApi().getPlayerManager().sendPlayer(account);
        super.handleLogout(player);
    }

    @Override
    public void start() {
        super.start();

        this.initBorder();

        final HyriPositionCalculator calculator = new HyriPositionCalculator(plugin);

        new HyriPositionCalculator.Cage(calculator.getCuboidCenter()).setCage();

        this.teleportPlayers(calculator, () -> {
            players.forEach(hyriRunnerGamePlayer -> {
                this.arrow = new HyriRunnerArrow(hyriRunnerGamePlayer.getPlayer());
                this.arrow.runTaskTimer(plugin, 0, 5);
            });

            this.players.forEach(gamePlayer -> this.setupInventory(gamePlayer.getPlayer()));

            this.sendMessageToAll(player -> HyriRunnerMessages.PREPARATION.get().getForPlayer(player));

            new BukkitRunnable() {

                private int index = 15;

                @Override
                public void run() {
                    players.forEach(gamePlayer -> gamePlayer.getPlayer().setLevel(index));

                    if (index == 0) {
                        calculator.removeCages();
                        gameTask = new HyriRunnerGameTask(plugin);

                        players.forEach(gamePlayer -> {
                            final HyriRunnerScoreboard scoreboard = new HyriRunnerFirstPhaseScoreboard(plugin, gamePlayer.getPlayer());

                            gamePlayer.setScoreboard(scoreboard);

                            scoreboard.show();
                        });

                        gameTask.runTaskTimer(plugin, 0, 20);

                        detectBorderEnd();

                        canPlace = true;

                        cancel();
                    }

                    index--;
                }
            }.runTaskTimer(plugin, 0, 20);
        });
    }

    public List<HyriRunnerGamePlayer> getPositionLead() {
        List<HyriRunnerGamePlayer> list = new ArrayList<>();

        players.forEach(gamePlayer -> {
            if (!gamePlayer.isSpectator()) {
                list.add(gamePlayer);
            } else {
                list.remove(gamePlayer);
            }
        });

        list.sort(Comparator.comparingInt(HyriRunnerGamePlayer::getDistance));
        return list;
    }

    public HyriGameTeam getWinner() {
        HyriGameTeam winner = null;
        for (HyriGameTeam team : this.teams) {
            if (team.hasPlayersPlaying()) {
                if (winner != null) {
                    return null;
                } else {
                    winner = team;
                }
            }
        }
        return winner;
    }

    @Override
    public void win(HyriGameTeam winner) {
        super.win(winner);
        if(winner != null) {
            this.gameTask.cancel();
            winner.getPlayers().forEach(player -> {
                HyriRunnerGamePlayer hyriGamePlayer = this.getPlayer(player.getUUID());
                for (HyriRunnerChallengeModel value : HyriRunnerChallengeModel.values()) {
                    Optional<HyriRunnerChallenge> oChallenge = HyriRunnerChallenge.getWithModel(value);
                    oChallenge.ifPresent(challenge -> {
                        if(hyriGamePlayer.getChallenge() != null) {
                            if(hyriGamePlayer.getChallenge().getCondition(hyriGamePlayer)) {
                                hyriGamePlayer.getChallenge().getReward(hyriGamePlayer);
                            } else hyriGamePlayer.sendMessage(HyriRunnerMessages.CHALLENGE_FAILED.get().getForPlayer(hyriGamePlayer.getPlayer())
                                    .replace("%challenge%", HyriRunner.getLanguageManager().getMessage(hyriGamePlayer.getChallenge().getKey()).getForPlayer(hyriGamePlayer.getPlayer())));
                        }
                    });
                }
            });
        }
    }

    private void teleportPlayers(HyriPositionCalculator calculator, HyriRunnerSafeTeleport.Callback callback) {
        final HyriRunnerSafeTeleport safeTeleport = new HyriRunnerSafeTeleport(plugin);

        safeTeleport.setCallback(callback);
        safeTeleport.teleportPlayers(calculator.getLocation());
        sendMessageToAll(player -> HyriRunnerMessages.INIT_TELEPORTATION.get().getForPlayer(player));
    }

    private void setupInventory(Player player) {
        ItemBuilder helmet = new ItemBuilder(Material.IRON_HELMET).unbreakable().withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        ItemBuilder chestPlate = new ItemBuilder(Material.DIAMOND_CHESTPLATE).unbreakable().withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        ItemBuilder leggings = new ItemBuilder(Material.IRON_LEGGINGS).unbreakable().withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        ItemBuilder boots = new ItemBuilder(Material.DIAMOND_BOOTS).unbreakable().withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        ItemBuilder sword = new ItemBuilder(Material.IRON_SWORD).unbreakable().withEnchant(Enchantment.DAMAGE_ALL, 2);
        ItemBuilder steaks = new ItemBuilder(Material.COOKED_BEEF, 64);
        ItemBuilder cobWebs = new ItemBuilder(Material.WEB, 1);
        ItemBuilder gaps = new ItemBuilder(Material.GOLDEN_APPLE, 5);
        ItemStack bucket = new ItemBuilder(Material.WATER_BUCKET).build();
        ItemStack blocks = new ItemBuilder(Material.STONE, 64).build();
        ItemStack wood = new ItemBuilder(Material.WOOD, 64).build();
        ItemStack pick = new ItemBuilder(Material.IRON_PICKAXE).unbreakable().build();

        player.getEquipment().setHelmet(helmet.build());
        player.getEquipment().setChestplate(chestPlate.build());
        player.getEquipment().setLeggings(leggings.build());
        player.getEquipment().setBoots(boots.build());
        player.getInventory().addItem(sword.build(), steaks.build(), cobWebs.build(), gaps.build(), bucket, bucket, blocks, blocks, wood, pick);
        player.setGameMode(GameMode.SURVIVAL);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 30, 15, false));
    }

    public void initBorder() {
        wb = Bukkit.getWorld(plugin.getGameMap().getName()).getWorldBorder();
        wb.setCenter(0, 0);
        wb.setSize(1500 * 2);
        wb.setWarningDistance(25);
    }

    public void startBorderShrink() {
        double time = Math.floor((1500.0 - 50.0) / 6.0);
        wb.setSize(50, (long) time);
    }

    public void detectBorderEnd() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (wb.getSize() <= 51) {
                    borderEnd = true;
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0, 20);
    }

    public List<HyriRunnerGamePlayer> getArrivedPlayers() {
        return this.players.stream().filter(HyriRunnerGamePlayer::isArrived).collect(Collectors.toList());
    }

    public boolean isPvp() {
        return pvp;
    }

    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }

    public boolean isDamage() {
        return damage;
    }

    public void setDamage(boolean damage) {
        this.damage = damage;
    }

    public boolean isBorderEnd() {
        return borderEnd;
    }

    public void setBorderEnd(boolean borderEnd) {
        this.borderEnd = borderEnd;
    }

    public HyriRunnerGameTask getGameTask() {
        return gameTask;
    }

    public boolean isAccessible() {
        return this.accessible;
    }

    public void setAccessible(boolean accessible) {
        this.accessible = accessible;
    }

    public boolean isCanPlace() {
        return canPlace;
    }

    public HyriRunnerArrow getArrow() {
        return this.arrow;
    }

}
