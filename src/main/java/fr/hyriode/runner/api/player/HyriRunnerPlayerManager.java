package fr.hyriode.runner.api.player;

import fr.hyriode.runner.api.HyriRunnerApi;

import java.util.UUID;
import java.util.function.Function;

public class HyriRunnerPlayerManager {

    private static final Function<UUID, String> REDIS_KEY = uuid -> HyriRunnerApi.REDIS_KEY + "players:" + uuid.toString();

    private final HyriRunnerApi api;

    public HyriRunnerPlayerManager(HyriRunnerApi api) {
        this.api = api;
    }

    public HyriRunnerPlayer getPlayer(UUID uuid) {
        final String json = this.api.getFromRedis(REDIS_KEY.apply(uuid));

        if (json != null) {
            return HyriRunnerApi.GSON.fromJson(json, HyriRunnerPlayer.class);
        }
        return null;
    }

    public void sendPlayer(HyriRunnerPlayer player) {
        this.api.redisRequest(jedis -> jedis.set(REDIS_KEY.apply(player.getUniqueId()), HyriRunnerApi.GSON.toJson(player)));
    }

    public void removePlayer(UUID uuid) {
        this.api.redisRequest(jedis -> jedis.del(REDIS_KEY.apply(uuid)));
    }
}
