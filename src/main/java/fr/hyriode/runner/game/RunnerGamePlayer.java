package fr.hyriode.runner.game;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.protocol.HyriLastHitterProtocol;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.RunnerChallengeModel;
import fr.hyriode.runner.api.RunnerPlayer;
import fr.hyriode.runner.api.RunnerStatistics;
import fr.hyriode.runner.challenge.RunnerChallenge;
import fr.hyriode.runner.game.scoreboard.RunnerFirstPhaseScoreboard;
import fr.hyriode.runner.game.scoreboard.RunnerScoreboard;
import fr.hyriode.runner.util.RunnerMessage;
import fr.hyriode.runner.util.RunnerValues;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

public class RunnerGamePlayer extends HyriGamePlayer {

    private int position;
    private long arrivedTime;

    private HyriRunner plugin;

    private RunnerScoreboard scoreboard;

    private RunnerPlayer account;
    private RunnerStatistics statistics;

    private int kills;
    private long deaths;

    private RunnerChallenge challenge;

    public RunnerGamePlayer(HyriGame<?> game, Player player) {
        super(game, player);
    }

    public void startGame() {
        this.player.setGameMode(GameMode.SURVIVAL);
        this.player.setHealth(20.0F);

        this.giveInventory();
        this.setupScoreboard();
    }

    private void giveInventory() {
        final PlayerInventory inventory = this.player.getInventory();

        RunnerValues.INVENTORY.get().setTo(inventory);
    }

    public void kill() {
        final RunnerGame game = this.plugin.getGame();

        if (this.challenge != null) {
            for (RunnerChallengeModel value : RunnerChallengeModel.values()) {
                if (this.challenge.getModel() != value) {
                    continue;
                }

                if (this.challenge.isValid(this)) {
                    this.challenge.rewardPlayer(this);
                } else {
                    this.sendMessage(RunnerMessage.CHALLENGE_FAILED.asString(this.player).replace("%challenge%", this.challenge.getName(this.player)));
                }
            }
        }

        final PlayerInventory playerInventory = this.player.getInventory();

        for (ItemStack content : playerInventory.getContents()) {
            if(content != null) {
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
            gamePlayer.updateScoreboard();
        }
    }

    public int getCenterDistance() {
        final Location playerLocation = this.player.getLocation().clone();
        final Location centerLocation = new Location(IHyrame.WORLD.get(), 0, 0, 0);

        playerLocation.setY(0);

        return (int) playerLocation.distance(centerLocation);
    }

    public void setupScoreboard() {
        this.scoreboard = new RunnerFirstPhaseScoreboard(this.plugin, this.player);
        this.scoreboard.show();
    }

    public void updateScoreboard() {
        this.scoreboard.update();
    }

    public RunnerScoreboard getScoreboard() {
        return this.scoreboard;
    }

    public void setScoreboard(RunnerScoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Player getLastHitter() {
        final List<HyriLastHitterProtocol.LastHitter> lastHitters = this.game.getProtocolManager().getProtocol(HyriLastHitterProtocol.class).getLastHitters(this.player);

        if (lastHitters != null) {
            return lastHitters.get(0).asPlayer();
        }
        return null;
    }

    public RunnerPlayer getAccount() {
        return account;
    }

    public void setAccount(RunnerPlayer account) {
        this.account = account;
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

        final List<RunnerGamePlayer> players = this.plugin.getGame().getPlayers();

        if (players.size() == 1 && players.contains(this)) {
            this.game.win(this.team);
        }
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

