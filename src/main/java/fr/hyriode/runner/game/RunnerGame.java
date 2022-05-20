package fr.hyriode.runner.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.HyriGameType;
import fr.hyriode.hyrame.game.protocol.HyriDeathProtocol;
import fr.hyriode.hyrame.game.protocol.HyriLastHitterProtocol;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.util.HyriGameMessages;
import fr.hyriode.hyrame.game.util.HyriRewardAlgorithm;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.challenges.HyriRunnerChallengeModel;
import fr.hyriode.runner.api.player.HyriRunnerPlayer;
import fr.hyriode.runner.api.statistics.HyriRunnerStatistics;
import fr.hyriode.runner.challenges.RunnerChallenge;
import fr.hyriode.runner.game.teleport.RunnerSafeTeleport;
import fr.hyriode.runner.game.team.RunnerGameTeam;
import fr.hyriode.runner.game.team.RunnerGameTeams;
import fr.hyriode.runner.game.teleport.RunnerPositionCalculator;
import fr.hyriode.runner.inventories.RunnerChooseChallengeItem;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.text.SimpleDateFormat;
import java.util.*;

public class RunnerGame extends HyriGame<RunnerGamePlayer> {

    private Scoreboard scoreboard;
    private WorldBorder wb;

    private boolean accessible;
    private boolean pvp;
    private boolean damage;
    private boolean canPlace = false;
    private boolean borderEnd = false;
    private int playersPvpPhaseRemaining;

    private final List<RunnerGamePlayer> arrivedPlayers;

    private RunnerGameTask gameTask;
    private RunnerArrow arrow;

    private final HyriRunner plugin;

    public RunnerGame(IHyrame hyrame, HyriRunner plugin) {
        super(hyrame, plugin, HyriAPI.get().getGameManager().getGameInfo("therunner"), RunnerGamePlayer.class, HyriGameType.getFromData(RunnerGameType.values()));
       // DEV super(hyrame, plugin, HyriAPI.get().getGameManager().getGameInfo("therunner"), RunnerGamePlayer.class, RunnerGameType.SOLO);
        this.plugin = plugin;

        this.damage = false;
        this.pvp = false;
        this.accessible = false;
        this.arrivedPlayers = new ArrayList<>();

        this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        this.description = HyriLanguageMessage.get("message.runner.description");

        this.registerTeams();
    }

    private void registerTeams() {
        for (RunnerGameTeams value : RunnerGameTeams.values()) {
            this.registerTeam(new RunnerGameTeam(plugin, value, this.getType().getTeamSize()));
        }
    }

    @Override
    public void handleLogin(Player player) {
        super.handleLogin(player);

        player.setGameMode(GameMode.ADVENTURE);

        final UUID uuid = player.getUniqueId();
        final RunnerGamePlayer gamePlayer = this.getPlayer(uuid);

        gamePlayer.setPlugin(this.plugin);
        gamePlayer.setWarrior(false);

        HyriRunnerPlayer account = this.plugin.getApi().getPlayerManager().getPlayer(uuid);

        if (account == null) {
            account = new HyriRunnerPlayer(uuid);
        }

        gamePlayer.setAccount(account);

        RunnerChallenge.getWithModel(account.getLastSelectedChallenge()).ifPresent(challenge -> {
            gamePlayer.setChallenge(challenge);
            gamePlayer.sendMessage(RunnerMessage.LAST_CHALLENGE_USED.get().getForPlayer(player).replace("%challenge%", HyriRunner.getLanguageManager().getMessage(gamePlayer.getChallenge().getKey()).getForPlayer(player)));
        });

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.hyrame.getItemManager().giveItem(player, 4, RunnerChooseChallengeItem.class), 1);

        player.teleport(this.plugin.getConfiguration().getSpawn().asBukkit());
    }

    @Override
    public void handleLogout(Player player) {
        final UUID uuid = player.getUniqueId();
        final RunnerGamePlayer gamePlayer = this.getPlayer(uuid);
        final HyriRunnerPlayer account = gamePlayer.getAccount();
        final HyriRunnerStatistics statistics = account.getStatistics();

        if (!this.getState().isAccessible()) {
            gamePlayer.getScoreboard().hide();

            statistics.setPlayedTime(statistics.getPlayedTime() + gamePlayer.getPlayedTime());
            statistics.addGamesPlayed(1);
            statistics.addKills(gamePlayer.getKills());
            statistics.addDeaths(gamePlayer.getDeaths());

            if(isPvp()) {
                this.playersPvpPhaseRemaining -= 1;
            }
        }

        if(gamePlayer.getChallenge() != null) {
            account.setLastSelectedChallenge(gamePlayer.getChallenge().getModel());
        }

        this.plugin.getApi().getPlayerManager().sendPlayer(account);

        super.handleLogout(player);

        if (this.getState() == HyriGameState.PLAYING) {
            this.win(this.getWinner());
        }
    }

    @Override
    public void start() {
        super.start();

        this.initBorder();

        this.protocolManager.enableProtocol(new HyriLastHitterProtocol(this.hyrame, this.plugin, 15 * 20L));
        this.protocolManager.enableProtocol(new HyriDeathProtocol(this.hyrame, this.plugin, gamePlayer -> {
            ((RunnerGamePlayer) gamePlayer).kill();
            return false;
        }));

        final RunnerPositionCalculator calculator = new RunnerPositionCalculator();

        new RunnerPositionCalculator.Cage(calculator.getCuboidCenter()).setCage();

        this.teleportPlayers(calculator, () -> {
            final Objective displayNameLife = scoreboard.registerNewObjective("vie", "health");
            final Objective playerListLife = scoreboard.registerNewObjective("vieb", "health");

            displayNameLife.setDisplayName(ChatColor.RED + "❤");
            displayNameLife.setDisplaySlot(DisplaySlot.BELOW_NAME);
            playerListLife.setDisplayName(ChatColor.RED + "❤");
            playerListLife.setDisplaySlot(DisplaySlot.PLAYER_LIST);

            players.forEach(gamePlayer -> {
                this.arrow = new RunnerArrow(gamePlayer.getPlayer());
                this.arrow.runTaskTimer(plugin, 0, 5);

                displayNameLife.getScore(gamePlayer.getPlayer().getName()).setScore((int) gamePlayer.getPlayer().getHealth());
                playerListLife.getScore(gamePlayer.getPlayer().getName()).setScore((int) gamePlayer.getPlayer().getHealth());

                gamePlayer.startGame();
            });

            this.sendMessageToAll(player -> RunnerMessage.PREPARATION.get().getForPlayer(player));

            this.getPreGameTask(calculator).runTaskTimer(plugin, 0, 20);
        });
    }

    private BukkitRunnable getPreGameTask(RunnerPositionCalculator calculator) {
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
                    gameTask = new RunnerGameTask(plugin);

                    players.forEach(gamePlayer -> {
                        final Player p = gamePlayer.getPlayer();

                        p.playSound(p.getLocation(), Sound.LEVEL_UP, 3f, 3f);
                    });

                    gameTask.runTaskTimer(plugin, 0, 20);

                    detectBorderEnd();

                    canPlace = true;

                    cancel();
                }

                index--;
            }
        };
    }

    public List<RunnerGamePlayer> getPositionLead() {
        List<RunnerGamePlayer> list = new ArrayList<>();

        players.forEach(gamePlayer -> {
            if (!gamePlayer.isSpectator()) {
                list.add(gamePlayer);
            } else {
                list.remove(gamePlayer);
            }
        });

        list.sort(Comparator.comparingInt(RunnerGamePlayer::getDistance));

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
            } else if (team.getPlayersPlaying().size() < winner.getPlayers().size()) {
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

    @Override
    public void win(HyriGameTeam winner) {
        super.win(winner);

        if (winner == null || this.getState() != HyriGameState.ENDED) {
            return;
        }

        this.gameTask.cancel();

        this.players.forEach(player -> {
            final RunnerGamePlayer gamePlayer = this.getPlayer(player.getUUID());
            final boolean isWinner = winner.contains(gamePlayer);


            if (isWinner) {
                gamePlayer.getAccount().getStatistics().addVictories(1);
            }

            final RunnerChallenge challenge = gamePlayer.getChallenge();
            final Player p = gamePlayer.getPlayer();
            final List<String> position = new ArrayList<>();

            for (int i = 0; i <= 2; i++) {
                final RunnerGamePlayer endPlayer = this.arrivedPlayers.size() > i ? this.arrivedPlayers.get(i) : null;
                final String line = HyriLanguageMessage.get("message.game.end.position").getForPlayer(p).replace("%position%", HyriLanguageMessage.get("message.game.end." + (i + 1)).getForPlayer(p));

                if (endPlayer == null) {
                    position.add(line.replace("%player%", HyriLanguageMessage.get("message.game.end.nobody").getForPlayer(p))
                            .replace("%time%", "00:00"));
                    continue;
                }

                final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

                format.setTimeZone(TimeZone.getTimeZone("GMT"));

                final String time = format.format(endPlayer.getArrivedTime() * 1000);
                final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(endPlayer.getUUID());

                position.add(line.replace("%player%", account.getNameWithRank(true))
                        .replace("%time%", time.startsWith("00:") ? time.substring(3) : time));
            }

            final long hyris = HyriRewardAlgorithm.getHyris(gamePlayer.getKills(), gamePlayer.getPlayedTime(), isWinner);
            final long xp = HyriRewardAlgorithm.getXP(gamePlayer.getKills(), gamePlayer.getPlayedTime(), isWinner);
            final List<String> rewards = new ArrayList<>();

            rewards.add(ChatColor.LIGHT_PURPLE + String.valueOf(hyris) + " Hyris");
            rewards.add(ChatColor.GREEN + String.valueOf(xp) + " XP");

            final IHyriPlayer account = gamePlayer.asHyriode();

            account.getHyris().add(hyris).withMessage(false).exec();
            account.getNetworkLeveling().addExperience(xp);
            account.update();

            p.spigot().sendMessage(HyriGameMessages.createWinMessage(this, p, winner, position, rewards));

            for (HyriRunnerChallengeModel value : HyriRunnerChallengeModel.values()) {
                RunnerChallenge.getWithModel(value).ifPresent(ch -> {
                    if (challenge != null && challenge.equals(ch)) {
                        if( challenge.getCondition(gamePlayer)) {
                            challenge.getReward(gamePlayer);
                        } else {
                            gamePlayer.sendMessage(RunnerMessage.CHALLENGE_FAILED.get().getForPlayer(p).replace("%challenge%", HyriRunner.getLanguageManager().getMessage(challenge.getKey()).getForPlayer(p)));
                        }
                    }
                });
            }
        });
    }

    private void teleportPlayers(RunnerPositionCalculator calculator, RunnerSafeTeleport.Callback callback) {
        final RunnerSafeTeleport safeTeleport = new RunnerSafeTeleport(plugin);

        safeTeleport.setCallback(callback);
        safeTeleport.teleportPlayers(calculator.getLocation());

        this.sendMessageToAll(player -> RunnerMessage.INIT_TELEPORTATION.get().getForPlayer(player));
    }

    public void initBorder() {
        wb = Bukkit.getWorld(HyriRunner.GAME_MAP).getWorldBorder();
        wb.setCenter(0, 0);
        wb.setSize(1500 * 2);
        wb.setWarningDistance(25);
    }

    public void startBorderShrink() {
        wb.setSize(50, (long) Math.floor((1500.0 - 50.0) / 6.0));
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

    public List<RunnerGamePlayer> getArrivedPlayers() {
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

    public RunnerGameTask getGameTask() {
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

    public RunnerArrow getArrow() {
        return this.arrow;
    }

    public int getPlayersPvpPhaseRemaining() {
        return playersPvpPhaseRemaining;
    }

    public void setPlayersPvpPhaseRemaining(int playersPvpPhaseRemaining) {
        this.playersPvpPhaseRemaining = playersPvpPhaseRemaining;
    }

    @Override
    public RunnerGameType getType() {
        return (RunnerGameType) super.getType();
    }
}
