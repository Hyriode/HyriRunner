package fr.hyriode.runner.game;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.protocol.HyriDeathProtocol;
import fr.hyriode.hyrame.game.protocol.HyriLastHitterProtocol;
import fr.hyriode.hyrame.game.protocol.HyriWaitingProtocol;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.util.HyriGameItems;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.utils.PlayerUtil;
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
import fr.hyriode.runner.inventories.HyriRunnerChooseChallengeItem;
import org.bukkit.*;
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
    private int playersPvpPhaseRemaining;

    private List<HyriRunnerGamePlayer> arrivedPlayers;

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
        this.arrivedPlayers = new ArrayList<>();

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
        player.setGameMode(GameMode.ADVENTURE);

        final UUID uuid = player.getUniqueId();
        final HyriRunnerGamePlayer gamePlayer = this.getPlayer(uuid);

        gamePlayer.setPlugin(this.plugin);
        gamePlayer.setWarrior(false);

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

        Bukkit.getScheduler().runTaskLater(plugin, () -> this.hyrame.getItemManager().giveItem(player, 4, HyriRunnerChooseChallengeItem.class), 1);

        player.teleport(plugin.getConfiguration().getSpawn());
    }

    @Override
    public void postRegistration() {
        super.postRegistration();

        this.protocolManager.getProtocol(HyriWaitingProtocol.class).withTeamSelector(true);
    }

    @Override
    public void handleLogout(Player player) {
        final UUID uuid = player.getUniqueId();
        final HyriRunnerGamePlayer gamePlayer = this.getPlayer(uuid);
        final HyriRunnerPlayer account = gamePlayer.getAccount();
        final HyriRunnerStatistics statistics = account.getStatistics();

        if (this.getState() != HyriGameState.READY && this.getState() != HyriGameState.WAITING) {
            gamePlayer.getScoreboard().hide();

            statistics.setPlayedTime(gamePlayer.getPlayedTime());
            statistics.addGamesPlayed(1);
            statistics.addKills(gamePlayer.getKills());
            statistics.addDeaths(gamePlayer.getDeaths());

           // account.getCompletedChallenges().add(gamePlayer.getChallenge().getModel());
            if(isPvp()) {
                this.playersPvpPhaseRemaining -= 1;
            }
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

        this.protocolManager.enableProtocol(new HyriLastHitterProtocol(this.hyrame, this.plugin, 15 * 20L));
        this.protocolManager.enableProtocol(new HyriDeathProtocol(this.hyrame, this.plugin, gamePlayer -> {
            this.getPlayer(gamePlayer.getUUID()).kill();
            return false;
        }));

        final HyriPositionCalculator calculator = new HyriPositionCalculator(plugin);

        new HyriPositionCalculator.Cage(calculator.getCuboidCenter()).setCage();

        this.teleportPlayers(calculator, () -> {
            players.forEach(hyriRunnerGamePlayer -> {
                this.arrow = new HyriRunnerArrow(hyriRunnerGamePlayer.getPlayer());
                this.arrow.runTaskTimer(plugin, 0, 5);
            });
            this.players.forEach(HyriRunnerGamePlayer::startGame);
            this.sendMessageToAll(player -> HyriRunnerMessages.PREPARATION.get().getForPlayer(player));

            this.getPreGameTask(calculator).runTaskTimer(plugin, 0, 20);
        });
    }

    private BukkitRunnable getPreGameTask(HyriPositionCalculator calculator) {
        return new BukkitRunnable() {

            private int index = 15;

            @Override
            public void run() {
                players.forEach(gamePlayer -> gamePlayer.getPlayer().setLevel(index));

                if(index <= 3 && index != 0) {
                    players.forEach(gamePlayer -> gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), Sound.SUCCESSFUL_HIT, 3f, 3f));
                }
                if (index == 0) {
                    calculator.removeCages();
                    gameTask = new HyriRunnerGameTask(plugin);

                    players.forEach(hyriRunnerGamePlayer -> {
                        Player p = hyriRunnerGamePlayer.getPlayer();
                        p.playSound(p.getLocation(), Sound.LEVEL_UP, 3f, 3f);
                    });

                    players.forEach(HyriRunnerGamePlayer::setupScoreboard);

                    gameTask.runTaskTimer(plugin, 0, 20);

                    detectBorderEnd();

                    canPlace = true;

                    cancel();
                }

                index--;
            }
        };
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

                HyriRunnerGamePlayer gamePlayer = this.getPlayer(player.getUUID());
                HyriRunnerChallenge gamePlayerChallenge = gamePlayer.getChallenge();
                Player p = gamePlayer.getPlayer();

                for (HyriRunnerChallengeModel value : HyriRunnerChallengeModel.values()) {

                    Optional<HyriRunnerChallenge> oChallenge = HyriRunnerChallenge.getWithModel(value);
                    oChallenge.ifPresent(challenge -> {
                        if(gamePlayerChallenge != null) {
                           if(gamePlayerChallenge.equals(challenge)) {
                               if(gamePlayerChallenge.getCondition(gamePlayer)) {
                                   gamePlayerChallenge.getReward(gamePlayer);
                               } else {
                                   gamePlayer.sendMessage(HyriRunnerMessages.CHALLENGE_FAILED.get().getForPlayer(p)
                                           .replace("%challenge%", HyriRunner.getLanguageManager().getMessage(gamePlayerChallenge.getKey()).getForPlayer(p)));
                               }
                           }
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
        return this.arrivedPlayers;
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

    public int getPlayersPvpPhaseRemaining() {
        return playersPvpPhaseRemaining;
    }

    public void setPlayersPvpPhaseRemaining(int playersPvpPhaseRemaining) {
        this.playersPvpPhaseRemaining = playersPvpPhaseRemaining;
    }

    public void removePlayerPvpPhaseRemaining() {
        this.playersPvpPhaseRemaining -= 1;
    }
}
