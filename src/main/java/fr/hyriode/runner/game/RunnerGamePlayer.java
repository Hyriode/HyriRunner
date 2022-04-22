package fr.hyriode.runner.game;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.protocol.HyriLastHitterProtocol;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.IHyriLanguageManager;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.api.challenges.HyriRunnerChallengeModel;
import fr.hyriode.runner.api.player.HyriRunnerPlayer;
import fr.hyriode.runner.challenges.RunnerChallenge;
import fr.hyriode.runner.game.scoreboard.RunnerFirstPhaseScoreboard;
import fr.hyriode.runner.game.scoreboard.RunnerScoreboard;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class RunnerGamePlayer extends HyriGamePlayer {

    private int position;
    private int blocksPlaced;
    private float arrivedTime;

    private HyriRunner plugin;

    private RunnerScoreboard scoreboard;

    private HyriRunnerPlayer account;

    private long kills;
    private long deaths;

    private RunnerChallenge challenge;
    private boolean warrior;

    public RunnerGamePlayer(HyriGame<?> game, Player player) {
        super(game, player);
    }

    public void startGame() {
        this.blocksPlaced = 0;

        this.giveInventory();
    }

    public void setupScoreboard() {
        this.scoreboard = new RunnerFirstPhaseScoreboard(this.plugin, this.player);
        this.scoreboard.show();
    }

    void giveInventory() {
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

        this.player.getEquipment().setHelmet(helmet.build());
        this.player.getEquipment().setChestplate(chestPlate.build());
        this.player.getEquipment().setLeggings(leggings.build());
        this.player.getEquipment().setBoots(boots.build());
        this.player.getInventory().addItem(sword.build(), steaks.build(), cobWebs.build(), gaps.build(), bucket, bucket, blocks, blocks, wood, pick);
        this.player.setGameMode(GameMode.SURVIVAL);
        this.player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 30, 15, false));
    }

    public HyriRunnerPlayer getAccount() {
        return account;
    }

    public void setAccount(HyriRunnerPlayer account) {
        this.account = account;
    }

    public boolean isWarrior() {
        return warrior;
    }

    public void setWarrior(boolean warrior) {
        this.warrior = warrior;
    }

    public int getDistance() {
        Location pLoc = new Location(Bukkit.getWorld(HyriRunner.GAME_MAP), player.getLocation().getX() , 0, player.getLocation().getZ());
        Location cLoc = new Location(Bukkit.getWorld(HyriRunner.GAME_MAP), 0, 0, 0);
        return (int) pLoc.distance(cLoc);
    }

    public RunnerScoreboard getScoreboard() {
        return scoreboard;
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

    public void kill() {
        final RunnerGame game = this.plugin.getGame();

        for (HyriRunnerChallengeModel value : HyriRunnerChallengeModel.values()) {
            Optional<RunnerChallenge> oChallenge = RunnerChallenge.getWithModel(value);
            oChallenge.ifPresent(challenge -> {
                if(this.getChallenge() != null) {
                   if(this.getChallenge().equals(challenge)) {
                       if(this.getChallenge().getCondition(this)) {
                           this.getChallenge().getReward(this);
                       } else this.sendMessage(RunnerMessage.CHALLENGE_FAILED.get().getForPlayer(this.player)
                               .replace("%challenge%", HyriRunner.getLanguageManager().getMessage(this.getChallenge().getKey()).getForPlayer(this.player)));
                   }
                }
            });
        }

        final PlayerInventory playerInventory = this.player.getInventory();

        for (ItemStack content : playerInventory.getContents()) {
            if(content != null) {
                this.player.getWorld().dropItemNaturally(this.player.getLocation(), content);
            }
        }

        Bukkit.getWorld(HyriRunner.GAME_MAP).strikeLightningEffect(this.player.getLocation());

        this.addDeath();
        if(this.getLastHitter() != null) {
            this.plugin.getGame().getPlayer(this.getLastHitter()).addKill();
        }

        this.setSpectator(true);

        game.win(game.getWinner());
    }

    public Player getLastHitter() {
        final List<HyriLastHitterProtocol.LastHitter> lastHitters = this.game.getProtocolManager().getProtocol(HyriLastHitterProtocol.class).getLastHitters(this.player);

        if (lastHitters != null) {
            return lastHitters.get(0).asPlayer();
        }
        return null;
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
        this.plugin = plugin;
    }

    public boolean isArrived() {
        return this.plugin.getGame().getArrivedPlayers().contains(this);
    }

    public void setArrived(boolean arrived) {
        this.plugin.getGame().getArrivedPlayers().add(this);
    }

    public RunnerChallenge getChallenge() {
        return challenge;
    }

    public void setChallenge(RunnerChallenge challenge) {
        this.challenge = challenge;
    }

    public int getBlocksPlaced() {
        return blocksPlaced;
    }

    public void addBlockPlaced() {
        this.blocksPlaced += 1;
    }

    public float getArrivedTime() {
        return arrivedTime;
    }

    public void setArrivedTime(float arrivedTime) {
        this.arrivedTime = arrivedTime;
    }
}

