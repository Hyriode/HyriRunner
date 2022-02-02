package fr.hyriode.runner.game;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.language.IHyriLanguageManager;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.hyrame.utils.Symbols;
import fr.hyriode.hyriapi.settings.HyriLanguage;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.player.HyriRunnerPlayer;
import fr.hyriode.runner.game.scoreboard.HyriRunnerScoreboard;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Function;

public class HyriRunnerGamePlayer extends HyriGamePlayer {

    private int position;
    private HyriRunner plugin;
    private HyriRunnerScoreboard scoreboard;
    private Player lastHitter;
    private BukkitTask lastHitterTask;
    private HyriRunnerPlayer account;
    private long kills;
    private long deaths;

    public HyriRunnerGamePlayer(HyriGame<?> game, Player player) {
        super(game, player);
    }

    public HyriRunnerPlayer getAccount() {
        return account;
    }

    public void setAccount(HyriRunnerPlayer account) {
        this.account = account;
    }

    public void setLastHitter(Player lastHitter) {
        if (lastHitter != null) {
            if (!this.game.areInSameTeam(this.player, lastHitter)) {
                this.lastHitter = lastHitter;

                if (this.lastHitterTask != null) {
                    this.lastHitterTask.cancel();
                }

                this.lastHitterTask = new BukkitRunnable() {
                    @Override
                    public void run() {
                        setLastHitter(null);
                    }
                }.runTaskLater(this.plugin, 20 * 15L);
            }
        }
    }

    public int getDistance() {
        Location pLoc = new Location(Bukkit.getWorld(plugin.getGameMap().getName()), player.getLocation().getX() , 0, player.getLocation().getZ());
        Location cLoc = new Location(Bukkit.getWorld(plugin.getGameMap().getName()), 0, 0, 0);
        return (int) pLoc.distance(cLoc);
    }

    public HyriRunnerScoreboard getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(HyriRunnerScoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    public void kill() {
        final HyriRunnerGame game = this.plugin.getGame();

        this.hide();

        final PlayerInventory playerInventory = this.player.getInventory();

        for (ItemStack content : playerInventory.getContents()) {
            if(content != null) {
                this.player.getWorld().dropItemNaturally(this.player.getLocation(), content);
            }
        }
        playerInventory.setArmorContents(null);
        playerInventory.clear();

        this.player.setHealth(20.0F);
        this.player.teleport(new Location(Bukkit.getWorld(plugin.getGameMap().getName()), 0, 100, 0));

        this.addDeath();

        final IHyriLanguageManager languageManager = HyriRunner.getLanguageManager();
        final String formattedName = this.team.formatName(this.player);

        Function<Player, String> killMessage;
        if (this.lastHitter != null) {
            final HyriRunnerGamePlayer lastHitterGamePlayer = this.plugin.getGame().getPlayer(this.lastHitter.getUniqueId());
            final Function<Player, String> defaultMessage = target -> formattedName + ChatColor.GRAY + languageManager.getValue(target, "message.kill-by-player") + lastHitterGamePlayer.getTeam().formatName(this.lastHitter) + ChatColor.GRAY;

            lastHitterGamePlayer.addKill();
            lastHitterGamePlayer.getScoreboard().updateLines();
            killMessage = target -> defaultMessage.apply(target) + ".";

        } else {
            killMessage = target -> formattedName + ChatColor.GRAY + languageManager.getValue(target, "message.died");
        }

        this.scoreboard.updateLines();

        this.game.sendMessageToAll(killMessage);
        this.game.getPlayers().forEach(hyriGamePlayer -> {
            Player p = hyriGamePlayer.getPlayer();
            p.getWorld().strikeLightningEffect(p.getLocation());
        });

        HyriLanguageMessage deathTitle = new HyriLanguageMessage("title.death")
                .addValue(HyriLanguage.FR, ChatColor.DARK_AQUA + Symbols.ROTATED_SQUARE + " " + "MORT" + " " + Symbols.ROTATED_SQUARE)
                .addValue(HyriLanguage.EN, ChatColor.DARK_AQUA + Symbols.ROTATED_SQUARE + " " + "DEAD" + " " + Symbols.ROTATED_SQUARE);

        Title.sendTitle(player, deathTitle.getForPlayer(player), null, 1, 3*20, 1);
        this.eliminated = true;
        this.spectator = true;
        this.setDead(true);
        this.setSpectator(true);

        this.player.setGameMode(GameMode.ADVENTURE);
        this.player.setAllowFlight(true);
        this.player.setFlying(true);
        this.lastHitter = null;

        // TODO Give spectating objects

        game.win(game.getWinner());
    }


    public void addKill() {
        this.kills += 1;
    }

    public long getKills() {
        return this.kills;
    }

    public void addDeath() {
        this.deaths += 1;
    }

    public long getDeaths() {
        return this.deaths;
    }

    public void setPlugin(HyriRunner plugin) {
        this.plugin =plugin;
    }
}

