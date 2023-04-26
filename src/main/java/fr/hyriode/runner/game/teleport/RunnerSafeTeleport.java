package fr.hyriode.runner.game.teleport;

import fr.hyriode.hyrame.actionbar.ActionBar;
import fr.hyriode.hyrame.packet.PacketUtil;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.game.RunnerGamePlayer;
import fr.hyriode.runner.util.RunnerMessage;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class RunnerSafeTeleport implements Listener {

    private Runnable callback;

    private final int totalPlayers;
    private int teleportedPlayers;

    private final List<RunnerGamePlayer> players;

    private final Location location;

    public RunnerSafeTeleport(Location location) {
        this.location = location;
        this.players = HyriRunner.get().getGame().getPlayers();
        this.totalPlayers = this.players.size();
        this.teleportedPlayers = 0;
    }

    public void start() {
        final List<RunnerMapChunk> chunks = this.getChunksAround(this.location.getChunk(), 2);

        for (RunnerMapChunk chunk : chunks) {
            chunk.asBukkit(this.location.getWorld()).load(true);
        }

        this.teleportPlayers(this.location, chunks);
    }

    public void teleportPlayers(Location location, List<RunnerMapChunk> chunks) {
        if (this.teleportedPlayers == this.totalPlayers) {
            this.finished();
            return;
        }

        final RunnerGamePlayer gamePlayer = this.players.get(this.teleportedPlayers);

        if (gamePlayer == null) {
            return;
        }

        final Player player = gamePlayer.getPlayer();

        for (RunnerMapChunk chunk : chunks) {
            PacketUtil.sendPacket(player, new PacketPlayOutMapChunk(((CraftChunk) chunk.asBukkit(this.location.getWorld())).getHandle(), true, 65535));
        }

        HyriRunner.get().getGame().getPlayers().forEach(target -> {
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

        new BukkitRunnable() {
            @Override
            public void run() {
                teleportPlayers(location, chunks);
                cancel();
            }
        }.runTaskLater(HyriRunner.get(), 7L);
    }

    public void finished() {
        if (this.callback != null) {
            this.callback.run();
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
