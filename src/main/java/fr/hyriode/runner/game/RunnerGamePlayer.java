package fr.hyriode.runner.game;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.protocol.HyriLastHitterProtocol;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.RunnerChallengeModel;
import fr.hyriode.runner.api.RunnerData;
import fr.hyriode.runner.api.RunnerStatistics;
import fr.hyriode.runner.challenge.RunnerChallenge;
import fr.hyriode.runner.game.ui.RunnerArrow;
import fr.hyriode.runner.game.ui.RunnerPlayerTracker;
import fr.hyriode.runner.game.ui.scoreboard.RunnerFirstPhaseScoreboard;
import fr.hyriode.runner.game.ui.scoreboard.RunnerScoreboard;
import fr.hyriode.runner.game.ui.scoreboard.RunnerSecondPhaseScoreboard;
import fr.hyriode.runner.util.RunnerMessage;
import fr.hyriode.runner.util.RunnerValues;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

public class RunnerGamePlayer extends HyriGamePlayer {

    private int position;
    private long arrivedTime;

    private HyriRunner plugin;

    private RunnerScoreboard scoreboard;
    private RunnerArrow arrow;
    private RunnerPlayerTracker playerTracker;

    private RunnerData data;
    private RunnerStatistics statistics;

    private int kills;
    private long deaths;

    private RunnerChallenge challenge;

    public RunnerGamePlayer(Player player) {
        super(player);
    }

    public void onStart() {
        this.player.setGameMode(GameMode.SURVIVAL);
        this.player.setHealth(20.0F);

        this.arrow = new RunnerArrow(this);
        this.arrow.runTaskTimer(this.plugin, 0, 5);

        this.scoreboard = new RunnerFirstPhaseScoreboard(this.plugin, this.player);
        this.scoreboard.show();

        final PlayerInventory inventory = this.player.getInventory();

        RunnerValues.INVENTORY.get().setTo(inventory);
    }

    public void onPvp() {
        this.arrow.cancel();

        this.scoreboard.hide();
        this.scoreboard = new RunnerSecondPhaseScoreboard(plugin, player);
        this.scoreboard.show();

        this.playerTracker = new RunnerPlayerTracker(this);
        this.playerTracker.runTaskTimer(this.plugin, 0, 5L);
    }

    public void onLogout() {
        if (this.scoreboard != null) {
            this.scoreboard.hide();
        }

        if (this.arrow != null) {
            this.arrow.cancel();
        }

        if (this.playerTracker != null) {
            this.playerTracker.cancel();
        }
    }

    public void kill() {
        if (this.challenge != null) {
            for (RunnerChallengeModel value : RunnerChallengeModel.values()) {
                if (this.challenge.getModel() != value) {
                    continue;
                }

                if (this.challenge.isValid(this)) {
                    this.challenge.rewardPlayer(this);
                } else {
                    this.getPlayer().sendMessage(RunnerMessage.CHALLENGE_FAILED.asString(this.player).replace("%challenge%", this.challenge.getName(this.player)));
                }
            }
        }

        final PlayerInventory playerInventory = this.player.getInventory();

        for (ItemStack content : playerInventory.getContents()) {
            if (content != null) {
                this.player.getWorld().dropItemNaturally(this.player.getLocation(), content);
            }
        }

        IHyrame.WORLD.get().strikeLightningEffect(this.player.getLocation());

        this.addDeath();

        final Player lastHitter = this.getLastHitter();

        if (lastHitter != null) {
            this.plugin.getGame().getPlayer(lastHitter).addKill();
        }

        for (RunnerGamePlayer gamePlayer : this.plugin.getGame().getPlayers()) {
            gamePlayer.getScoreboard().update();
        }
    }

    public int getCenterDistance() {
        final Location playerLocation = this.player.getLocation().clone();
        final Location centerLocation = new Location(IHyrame.WORLD.get(), 0, 0, 0);

        playerLocation.setY(0);

        return (int) playerLocation.distance(centerLocation);
    }

    public RunnerScoreboard getScoreboard() {
        return this.scoreboard;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Player getLastHitter() {
        final List<HyriLastHitterProtocol.LastHitter> lastHitters = this.plugin.getGame().getProtocolManager().getProtocol(HyriLastHitterProtocol.class).getLastHitters(this.player);

        if (lastHitters != null) {
            return lastHitters.get(0).asPlayer();
        }
        return null;
    }

    public RunnerData getData() {
        return this.data;
    }

    public void setData(RunnerData data) {
        this.data = data;
    }

    public RunnerStatistics getStatistics() {
        return this.statistics;
    }

    public void setStatistics(RunnerStatistics statistics) {
        this.statistics = statistics;
    }

    public void setPlugin(HyriRunner plugin) {
        this.plugin = plugin;
    }

    public void addKill() {
        this.kills += 1;
    }

    public int getKills() {
        return this.kills;
    }

    public void addDeath() {
        this.deaths += 1;
    }

    public long getDeaths() {
        return this.deaths;
    }

    public boolean isArrived() {
        return this.plugin.getGame().getArrivedPlayers().contains(this);
    }

    public void setArrived() {
        this.plugin.getGame().getArrivedPlayers().add(this);
    }

    public RunnerChallenge getChallenge() {
        return this.challenge;
    }

    public void setChallenge(RunnerChallenge challenge) {
        this.challenge = challenge;
    }

    public long getArrivedTime() {
        return this.arrivedTime;
    }

    public void setArrivedTime(long arrivedTime) {
        this.arrivedTime = arrivedTime;
    }

}

