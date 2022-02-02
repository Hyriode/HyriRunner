package fr.hyriode.runner.game;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.util.HyriGameItems;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.player.HyriRunnerPlayer;
import fr.hyriode.runner.api.statistics.HyriRunnerStatistics;
import fr.hyriode.runner.game.gamemap.HyriRunnerSafeTeleport;
import fr.hyriode.runner.game.scoreboard.HyriRunnerFirstPhaseScoreboard;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;

public class HyriRunnerGame extends HyriGame<HyriRunnerGamePlayer> {

    private HyriRunner plugin;
    private WorldBorder wb;
    private boolean joinable;
    private boolean pvp;
    private boolean damage;
    private boolean canPlace = false;
    private boolean borderEnd = false;
    private HyriRunnerGameTask hyriRunnerGameTask;

    public HyriRunnerGame(IHyrame hyrame, HyriRunner plugin) {
        super(hyrame, plugin, "therunner", "TheRunner", HyriRunnerGamePlayer.class);

        this.plugin = plugin;
        this.maxPlayers = HyriRunnerGameType.getByName(plugin.getConfiguration().getGameType()).orElse(HyriRunnerGameType.SOLO).getTeamSize() * 12;
        this.minPlayers = this.maxPlayers/3;
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

        if(account == null) {
            account = new HyriRunnerPlayer(uuid);
        }

        gamePlayer.setAccount(account);
        gamePlayer.setConnectionTime();

        HyriGameItems.TEAM_CHOOSER.give(this.hyrame, player, 0);
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

            this.plugin.getApi().getPlayerManager().sendPlayer(account);
        }
        super.handleLogout(player);
    }

    @Override
    public void start() {
        super.start();

        this.initBorder();
        HyriPositionCalculator calculator = new HyriPositionCalculator(plugin);
        new HyriPositionCalculator.Cage(calculator.getCuboidCenter()).setCage();
        this.teleportPlayers(calculator, (gamePlayers) -> {
            HyriRunnerArrow arrow = new HyriRunnerArrow(plugin);
            arrow.schedule();
            gamePlayers.forEach(hyriRunnerGamePlayer -> this.setupInventory(hyriRunnerGamePlayer.getPlayer()));
            this.sendMessageToAll(player -> HyriRunnerMessages.PREPARATION.get().getForPlayer(player));
            new BukkitRunnable() {
                private int index = 15;

                @Override
                public void run() {
                    gamePlayers.forEach(hyriRunnerGamePlayer -> hyriRunnerGamePlayer.getPlayer().setLevel(index));
                    if (index == 0) {
                        calculator.removeCages();
                        hyriRunnerGameTask = new HyriRunnerGameTask(plugin);
                        gamePlayers.forEach(hyriRunnerGamePlayer -> {
                            hyriRunnerGamePlayer.setScoreboard(new HyriRunnerFirstPhaseScoreboard(plugin, hyriRunnerGamePlayer.getPlayer()));
                        });
                        hyriRunnerGameTask.runTaskTimer(plugin, 0, 20);
                        gamePlayers.forEach(hyriRunnerGamePlayer -> hyriRunnerGamePlayer.getScoreboard().show());
                        detectBorderEnd();
                        canPlace = true;
                        cancel();
                    }
                    index--;
                }
            }.runTaskTimer(plugin, 0, 20);
        });
    }

    public ArrayList<HyriRunnerGamePlayer> getPositionLead(HyriRunner plugin) {
        ArrayList<HyriRunnerGamePlayer> l = new ArrayList<>();
                players.forEach(hyriRunnerGamePlayer -> {
                    if(!hyriRunnerGamePlayer.isSpectator()) {
                        l.add(hyriRunnerGamePlayer);
                    } else {
                        l.remove(hyriRunnerGamePlayer);
                    }
                });
                l.sort(Comparator.comparingInt(HyriRunnerGamePlayer::getDistance));
        return l;
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
        this.hyriRunnerGameTask.cancel();
    }

    private void teleportPlayers(HyriPositionCalculator calculator, HyriRunnerSafeTeleport.ISafeTeleport iSafeTeleport) {
        HyriRunnerSafeTeleport safeTeleport = new HyriRunnerSafeTeleport(plugin, players);
        safeTeleport.setiSafeTeleport(iSafeTeleport);
        safeTeleport.teleportPlayers(calculator.getLocation());
    }

    public void setupInventory(Player player) {
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
                if(wb.getSize() == 50) {
                    borderEnd = true;
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }
    public void deleteBorder() {
        wb.reset();
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

    public HyriRunnerGameTask getHyriRunnerGameTask() {
        return hyriRunnerGameTask;
    }

    public boolean isJoinable() {
        return joinable;
    }

    public void setJoinable(boolean joinable) {
        this.joinable = joinable;
    }

    public boolean isCanPlace() {
        return canPlace;
    }
}
