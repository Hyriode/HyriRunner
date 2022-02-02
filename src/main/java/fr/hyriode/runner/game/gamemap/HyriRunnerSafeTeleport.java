package fr.hyriode.runner.game.gamemap;

import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.listener.HyriListener;
import fr.hyriode.hyriapi.settings.HyriLanguage;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.game.HyriRunnerGamePlayer;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class HyriRunnerSafeTeleport extends HyriListener<HyriRunner> {

    private HyriRunner plugin;
    private List<HyriRunnerGamePlayer> players;
    private ArrayList<HyriRunnerGamePlayer> playersCopy;
    private int total;
    private int already;
    private Instant before;
    private ISafeTeleport iSafeTeleport;
    private static HyriLanguageMessage msg = new HyriLanguageMessage("actionbar.teleport")
            .addValue(HyriLanguage.FR, ChatColor.DARK_AQUA+ "Téléportation en cours : " +ChatColor.AQUA+ "%already%/%total%")
            .addValue(HyriLanguage.EN, ChatColor.DARK_AQUA+ "Teleport in progress: " +ChatColor.AQUA+ "%already%/%total%");


    public HyriRunnerSafeTeleport(HyriRunner plugin, List<HyriRunnerGamePlayer> players) {
        super(plugin);
        this.plugin = plugin;
        this.players = players;
        this.playersCopy = new ArrayList<>(players);
        this.total = players.size();
        this.already = 0;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    public void teleportPlayers(Location l) {
        if (players.isEmpty()) {
            finished();
            return;
        }

        HyriRunnerGamePlayer gamePlayer = players.get(0);
        Player player = gamePlayer.getPlayer();
        ArrayList<HyriRunnerMapChunks> chunkCoords = around(l.getChunk(), Bukkit.getViewDistance());

        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        PlayerConnection playerConnection = entityPlayer.playerConnection;
        for (HyriRunnerMapChunks chunkCoord : chunkCoords) {
            Chunk c = l.getWorld().getChunkAt(chunkCoord.getX(), chunkCoord.getZ());
            net.minecraft.server.v1_8_R3.Chunk nmsc = ((CraftChunk) c).getHandle();

            PacketPlayOutMapChunk p = new PacketPlayOutMapChunk(nmsc, true, 20);
            playerConnection.sendPacket(p);
        }
        player.teleport(l);
        player.getActivePotionEffects().forEach(potionEffect ->
                player.removePotionEffect(potionEffect.getType()));
        before = Instant.now();
        new BukkitRunnable() {
            @Override
            public void run() {
                if(Duration.between(before, Instant.now()).toMillis() >= 350) {
                    players.remove(gamePlayer);
                    teleportPlayers(l);
                    already++;
                    plugin.getGame().getPlayers().forEach(hyriRunnerGamePlayer -> {
                        Player p = hyriRunnerGamePlayer.getPlayer();
                        ActionBar bar = new ActionBar(msg.getForPlayer(p)
                                .replace("%already%", String.valueOf(already))
                                .replace("%total%", String.valueOf(total))
                        );
                        bar.send(p);
                    });
                    cancel();
                }
            }
        }.runTaskTimer(this.plugin, 0,1);
    }

    public void finished() {
        HandlerList.unregisterAll(this);
        if(this.iSafeTeleport != null) {
            this.iSafeTeleport.onComplete(playersCopy);
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        if(before != null) {
            before = Instant.now();
        }
    }


    private ArrayList<HyriRunnerMapChunks> around(Chunk origin, int radius) {

        World world =  origin.getWorld();

        int length = (radius * 2) + 1;
        ArrayList<HyriRunnerMapChunks> chunks = new ArrayList<>(length * length);

        int cX = origin.getX();
        int cZ = origin.getZ();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                chunks.add(new HyriRunnerMapChunks(cX + x, cZ + z));
            }
        }
        return chunks;
    }

    public void setiSafeTeleport(ISafeTeleport iSafeTeleport) {
        this.iSafeTeleport = iSafeTeleport;
    }

    public interface ISafeTeleport {
        public void onComplete(List<HyriRunnerGamePlayer> players);
    }

}
