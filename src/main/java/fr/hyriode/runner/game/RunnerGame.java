package fr.hyriode.runner.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.leaderboard.IHyriLeaderboardProvider;
import fr.hyriode.api.leveling.NetworkLeveling;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.HyriGameType;
import fr.hyriode.hyrame.game.protocol.HyriDeathProtocol;
import fr.hyriode.hyrame.game.protocol.HyriHealthDisplayProtocol;
import fr.hyriode.hyrame.game.protocol.HyriLastHitterProtocol;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.util.HyriGameMessages;
import fr.hyriode.hyrame.game.util.HyriRewardAlgorithm;
import fr.hyriode.hyrame.scoreboard.team.HyriScoreboardTeam;
import fr.hyriode.hyrame.utils.BroadcastUtil;
import fr.hyriode.hyrame.utils.TimeUtil;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.RunnerData;
import fr.hyriode.runner.api.RunnerStatistics;
import fr.hyriode.runner.challenge.RunnerChallenge;
import fr.hyriode.runner.challenge.item.RunnerChallengeSelectorItem;
import fr.hyriode.runner.game.phase.RunnerPhase;
import fr.hyriode.runner.game.phase.RunnerPhaseTriggeredEvent;
import fr.hyriode.runner.game.team.RunnerGameTeam;
import fr.hyriode.runner.game.teleport.RunnerCage;
import fr.hyriode.runner.game.teleport.RunnerSafeTeleport;
import fr.hyriode.runner.game.ui.scoreboard.RunnerScoreboard;
import fr.hyriode.runner.game.ui.scoreboard.RunnerSpectatorScoreboard;
import fr.hyriode.runner.util.RunnerMessage;
import fr.hyriode.runner.util.RunnerValues;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;

public class RunnerGame extends HyriGame<RunnerGamePlayer> {

    private WorldBorder border;

    private final Set<RunnerPhase> phases;

    private final List<RunnerGamePlayer> arrivedPlayers;

    private RunnerGameTask gameTask;

    private final RunnerCage cage;
    private final RunnerSafeTeleport safeTeleport;

    private final HyriRunner plugin;

    public RunnerGame(IHyrame hyrame, HyriRunner plugin) {
        super(hyrame, plugin,
                HyriAPI.get().getConfig().isDevEnvironment() ? HyriAPI.get().getGameManager().createGameInfo("therunner", "TheRunner") : HyriAPI.get().getGameManager().getGameInfo("therunner"),
                RunnerGamePlayer.class,
                HyriAPI.get().getConfig().isDevEnvironment() ? RunnerGameType.SOLO : HyriGameType.getFromData(RunnerGameType.values()));
        this.plugin = plugin;
        this.phases = new HashSet<>();
        this.arrivedPlayers = new ArrayList<>();
        this.waitingRoom = new RunnerWaitingRoom(this, plugin);
        this.description = HyriLanguageMessage.get("message.runner.description");
        this.reconnectionTime = 60;

        this.border = Bukkit.getWorld(HyriRunner.GAME_MAP).getWorldBorder();
        this.border.setCenter(0, 0);
        this.border.setSize(1500L * 2);
        this.border.setWarningDistance(25);

        this.cage = new RunnerCage();
        this.cage.create();
        this.safeTeleport = new RunnerSafeTeleport(this.cage.getLocation());
        this.safeTeleport.loadChunks();

        this.registerTeams();
    }

    private void registerTeams() {
        for (RunnerGameTeam value : RunnerGameTeam.values()) {
            this.registerTeam(new HyriGameTeam(value.getName(), value.getDisplayName(), value.getColor(), false, HyriScoreboardTeam.NameTagVisibility.ALWAYS, this.getType().getTeamSize()));
        }
    }

    @Override
    public void handleLogin(Player player) {
        super.handleLogin(player);

        final UUID uuid = player.getUniqueId();
        final RunnerGamePlayer gamePlayer = this.getPlayer(uuid);
        final RunnerData data = RunnerData.get(uuid);
        final RunnerStatistics statistics = RunnerStatistics.get(uuid);

        gamePlayer.setPlugin(this.plugin);
        gamePlayer.setData(data);
        gamePlayer.setStatistics(statistics);

        if (!HyriAPI.get().getServer().getAccessibility().equals(HyggServer.Accessibility.HOST) && this.getState().isAccessible()) {
            RunnerChallenge.getWithModel(data.getLastChallenge()).ifPresent(challenge -> {
                gamePlayer.setChallenge(challenge);
                gamePlayer.getPlayer().sendMessage(RunnerMessage.LAST_CHALLENGE_USED.asString(player).replace("%challenge%", gamePlayer.getChallenge().getName(player)));
            });

            this.hyrame.getItemManager().giveItem(player, 4, RunnerChallengeSelectorItem.class);
        }
    }

    @Override
    public void handleLogout(Player player) {
        final UUID uuid = player.getUniqueId();
        final RunnerGamePlayer gamePlayer = this.getPlayer(uuid);
        final IHyriPlayer account = gamePlayer.asHyriPlayer();
        final RunnerData data = gamePlayer.getData();
        final RunnerStatistics statistics = gamePlayer.getStatistics();
        final RunnerStatistics.Data statisticsData = statistics.getData(this.getType());
        final RunnerChallenge challenge = gamePlayer.getChallenge();

        if (!this.getState().isAccessible()) {
            gamePlayer.onLogout();

            statisticsData.addGamesPlayed(1);
            statisticsData.addKills(gamePlayer.getKills());
            statisticsData.addDeaths(gamePlayer.getDeaths());

            if (this.arrivedPlayers.contains(gamePlayer)) {
                statisticsData.addSuccessfulRun(1);
            }
        }

        if (challenge != null) {
            data.setLastChallenge(challenge.getModel());
        }

        statistics.update(account);
        data.update(account);

        super.handleLogout(player);

        if (this.getState() == HyriGameState.PLAYING) {
            IHyrame.get().getScoreboardManager().getScoreboards(RunnerScoreboard.class).forEach(RunnerScoreboard::update);

            this.win(this.getWinner());
        }
    }

    @Override
    public void start() {
        super.start();

        this.hyrame.getWorldProvider().setCurrentWorld(HyriRunner.GAME_MAP);

        this.protocolManager.enableProtocol(new HyriLastHitterProtocol(this.hyrame, this.plugin, 10 * 20L));
        this.protocolManager.enableProtocol(new HyriDeathProtocol(this.hyrame, this.plugin, gamePlayer -> {
            ((RunnerGamePlayer) gamePlayer).kill();
            return false;
        }));

        this.teleportPlayers(() -> {
            for (RunnerGamePlayer gamePlayer : this.players) {
                gamePlayer.onStart();
            }

            this.protocolManager.enableProtocol(new HyriHealthDisplayProtocol(this.hyrame, new HyriHealthDisplayProtocol.Options(true, true)));

            BroadcastUtil.broadcast(RunnerMessage.PREPARATION::asString);

            this.createPreGameTask(cage).runTaskTimer(this.plugin, 0, 20);
        });
    }

    private BukkitRunnable createPreGameTask(RunnerCage cage) {
        return new BukkitRunnable() {

            private int index = 20;

            @Override
            public void run() {
                players.forEach(gamePlayer -> gamePlayer.getPlayer().setLevel(index));

                if (index <= 3 && index != 0) {
                    players.forEach(gamePlayer -> {
                        if (!gamePlayer.isOnline()) {
                            return;
                        }

                        gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), Sound.SUCCESSFUL_HIT, 3f, 3f);
                    });
                }

                if (index == 0) {
                    this.cancel();

                    cage.remove();

                    gameTask = new RunnerGameTask(plugin);
                    gameTask.runTaskTimer(plugin, 0, 20);

                    players.forEach(gamePlayer -> {
                        if (!gamePlayer.isOnline()) {
                            return;
                        }

                        final Player player = gamePlayer.getPlayer();

                        player.playSound(player.getLocation(), Sound.LEVEL_UP, 3f, 3f);
                    });

                    triggerPhase(RunnerPhase.PLACE);
                    detectBorderEnd();
                }

                index--;
            }
        };
    }

    private void teleportPlayers(Runnable callback) {
        BroadcastUtil.broadcast(RunnerMessage.INIT_TELEPORTATION::asString);

        this.safeTeleport.setCallback(callback);
        this.safeTeleport.startTeleportation();
    }

    public void startBorderShrink() {
        this.border.setSize(RunnerValues.BORDER_FINAL_SIZE.get(), (long) Math.floor((RunnerValues.BORDER_INITIAL_SIZE.get() - RunnerValues.BORDER_FINAL_SIZE.get()) / RunnerValues.BORDER_SPEED.get()));
    }

    public void detectBorderEnd() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (border.getSize() <= RunnerValues.BORDER_FINAL_SIZE.get() + 1) {
                    reconnectionTime = -1;

                    triggerPhase(RunnerPhase.BORDER_END);
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0, 20);
    }

    @Override
    public void win(HyriGameTeam winner) {
        if (winner == null || this.getState() != HyriGameState.PLAYING) {
            return;
        }

        super.win(winner);

        for (HyriGamePlayer player : winner.getPlayers()) {
            final RunnerGamePlayer gamePlayer = player.cast();

            gamePlayer.getStatisticsData().addVictories(1);
        }

        this.gameTask.cancel();

        final Function<Player, List<String>> positionsProvider = player -> {
            final List<String> positions = new ArrayList<>();

            for (int i = 0; i <= 2; i++) {
                final RunnerGamePlayer endPlayer = this.arrivedPlayers.size() > i ? this.arrivedPlayers.get(i) : null;
                final String line = HyriLanguageMessage.get("message.game.end.position").getValue(player)
                        .replace("%position%", HyriLanguageMessage.get("message.game.end." + (i + 1)).getValue(player));

                if (endPlayer == null) {
                    positions.add(line.replace("%player%", HyriLanguageMessage.get("message.game.end.nobody").getValue(player))
                            .replace("%time%", "00:00"));
                    continue;
                }

                positions.add(line.replace("%player%", endPlayer.formatNameWithTeam()).replace("%time%", TimeUtil.formatTime(endPlayer.getArrivedTime())));
            }
            return positions;
        };

        // Send message to not-playing players
        for (Player player : Bukkit.getOnlinePlayers()) {
            final RunnerGamePlayer gamePlayer = this.getPlayer(player);

            if (gamePlayer == null) {
                player.spigot().sendMessage(HyriGameMessages.createWinMessage(this, player, winner, positionsProvider.apply(player), null));
            }
        }

        for (RunnerGamePlayer gamePlayer : this.players) {
            final Player player = gamePlayer.getPlayer();
            final IHyriPlayer account = gamePlayer.asHyriPlayer();
            final UUID playerId = gamePlayer.getUniqueId();
            final int kills = gamePlayer.getKills();
            final boolean isWinner = winner.contains(gamePlayer);
            final long hyris = account.getHyris().add(HyriRewardAlgorithm.getHyris(kills, gamePlayer.getPlayTime(), isWinner)).withMessage(false).exec();
            final double xp = account.getNetworkLeveling().addExperience(HyriRewardAlgorithm.getXP(kills, gamePlayer.getPlayTime(), isWinner));

            // Update leaderboards
            final IHyriLeaderboardProvider provider = HyriAPI.get().getLeaderboardProvider();

            provider.getLeaderboard(NetworkLeveling.LEADERBOARD_TYPE, "therunner-experience").incrementScore(playerId, xp);
            provider.getLeaderboard("therunner", "kills").incrementScore(playerId, kills);

            // Check for a new personal best run
            if (gamePlayer.isArrived()) {
                final RunnerStatistics.Data statisticsData = gamePlayer.getStatisticsData();

                if (statisticsData.getBestRun() <= 0 || statisticsData.getBestRun() > gamePlayer.getArrivedTime()) {
                    statisticsData.setBestRun(gamePlayer.getArrivedTime());
                    provider.getLeaderboard("therunner", "runs").setScore(playerId, gamePlayer.getArrivedTime());
                }
            }

            if (isWinner) {
                provider.getLeaderboard("therunner", "victories").incrementScore(playerId, 1);
            }

            account.update();

            // Send message
            final String rewardsLine = ChatColor.LIGHT_PURPLE + "+" + hyris + " Hyris " + ChatColor.GREEN + "+" + xp + " XP";

            if (gamePlayer.isOnline()) {
                player.spigot().sendMessage(HyriGameMessages.createWinMessage(this, gamePlayer.getPlayer(), winner, positionsProvider.apply(player), rewardsLine));
            } else if (HyriAPI.get().getPlayerManager().isOnline(playerId)) {
                HyriAPI.get().getPlayerManager().sendMessage(playerId, HyriGameMessages.createOfflineWinMessage(this, account, rewardsLine));
            }

            // Verify challenge
            final RunnerChallenge challenge = gamePlayer.getChallenge();

            if (challenge == null || gamePlayer.isDead()) {
                return;
            }

            if (challenge.isValid(gamePlayer)) {
                challenge.rewardPlayer(gamePlayer);
            } else if (gamePlayer.isOnline()){
                player.sendMessage(RunnerMessage.CHALLENGE_FAILED.asString(account).replace("%challenge%", gamePlayer.getChallenge().getName(player)));
            }
        }
    }

    public List<RunnerGamePlayer> getPlayersLeaderboard() {
        final List<RunnerGamePlayer> list = new ArrayList<>();

        this.players.forEach(gamePlayer -> {
            if (!gamePlayer.isSpectator() && gamePlayer.isOnline()) {
                list.add(gamePlayer);
            }
        });

        list.sort(Comparator.comparingInt(RunnerGamePlayer::getCenterDistance));

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

    public HyriGameTeam getBestTeam() {
        HyriGameTeam winner = null;
        for (HyriGameTeam team : this.teams) {
            if (winner == null) {
                winner = team;
            } else if (team.getPlayersPlaying().size() > winner.getPlayersPlaying().size()) {
                winner = team;
            } else if (team.getPlayersPlaying().size() == winner.getPlayers().size()) {
                RunnerGamePlayer bestPlayer = null;
                for (HyriGamePlayer player : team.getPlayers()) {
                    final RunnerGamePlayer gamePlayer = (RunnerGamePlayer) player;

                    if (bestPlayer == null) {
                        bestPlayer = gamePlayer;
                    } else if (gamePlayer.getArrivedTime() < bestPlayer.getArrivedTime()) {
                        bestPlayer = gamePlayer;
                    }
                }

                for (HyriGamePlayer player : winner.getPlayers()) {
                    final RunnerGamePlayer gamePlayer = (RunnerGamePlayer) player;

                    if (bestPlayer == null) {
                        bestPlayer = gamePlayer;
                    } else if (gamePlayer.getArrivedTime() < bestPlayer.getArrivedTime()) {
                        bestPlayer = gamePlayer;
                    }
                }
            }
        }
        return winner;
    }

    public List<RunnerGamePlayer> getArrivedPlayers() {
        return this.arrivedPlayers;
    }

    public void triggerPhase(RunnerPhase phase) {
        this.phases.add(phase);

        HyriAPI.get().getEventBus().publish(new RunnerPhaseTriggeredEvent(this, phase));
    }

    public boolean isPhase(RunnerPhase phase) {
        return this.phases.contains(phase);
    }

    public RunnerGameTask getGameTask() {
        return this.gameTask;
    }

    @Override
    public RunnerGameType getType() {
        return (RunnerGameType) super.getType();
    }

}
