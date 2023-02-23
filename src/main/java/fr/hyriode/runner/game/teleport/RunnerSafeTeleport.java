package fr.hyriode.runner.game.teleport;


import fr.hyriode.api.language.HyriLanguage;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.hyrame.packet.PacketUtil;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.game.RunnerGamePlayer;
import fr.hyriode.runner.util.RunnerMessage;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class RunnerSafeTeleport implements Listener {

    private Instant before;
    private Runnable callback;

    private final int totalPlayers;
    private int teleportedPlayers;

    private final List<RunnerGamePlayer> players;

    private final HyriRunner plugin;

    public RunnerSafeTeleport(HyriRunner plugin) {
        this.plugin = plugin;
        this.players = this.plugin.getGame().getPlayers();
        this.totalPlayers = this.players.size();
        this.teleportedPlayers = 0;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    public void teleportPlayers(Location location) {
        if (this.teleportedPlayers == this.totalPlayers) {
            this.finished();
            return;
        }

        final RunnerGamePlayer gamePlayer = this.players.get(this.teleportedPlayers);

        if (gamePlayer == null) {
            return;
        }

        final Player player = gamePlayer.getPlayer();
        final List<RunnerMapChunk> chunks = this.getChunksAround(location.getChunk(), Bukkit.getViewDistance());

        for (RunnerMapChunk chunk : chunks) {
            PacketUtil.sendPacket(player, new PacketPlayOutMapChunk(((CraftChunk) location.getWorld().getChunkAt(chunk.getX(), chunk.getZ())).getHandle(), true, 20));
        }

        plugin.getGame().getPlayers().forEach(target -> {
            if (!target.isOnline()) {
                return;
            }

            final ActionBar bar = new ActionBar(RunnerMessage.TELEPORTATION_BAR.asString(target.getPlayer())
                    .replace("%already%", String.valueOf(this.teleportedPlayers))
                    .replace("%total%", String.valueOf(this.totalPlayers)));

            bar.send(target.getPlayer());
        });

        player.setFallDistance(0.0F);
        player.teleport(location);

        this.teleportedPlayers++;

        this.before = Instant.now();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (Duration.between(before, Instant.now()).toMillis() >= 350) {
                    teleportPlayers(location);
                    cancel();
                }
            }
        }.runTaskTimer(this.plugin, 0L, 1L);
    }

    public void finished() {
        HandlerList.unregisterAll(this);

        if (this.callback != null) {
            this.callback.run();
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        if (this.before != null) {
            this.before = Instant.now();
        }
    }

    private List<RunnerMapChunk> getChunksAround(Chunk origin, int radius) {
        final int length = (radius * 2) + 1;
        final List<RunnerMapChunk> chunks = new ArrayList<>(length * length);

        final int cX = origin.getX();
        final int cZ = origin.getZ();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                chunks.add(new RunnerMapChunk(cX + x, cZ + z));
            }
        }
        return chunks;
    }

    public void setCallback(Runnable callback) {
        this.callback = callback;
    }

}
