package fr.hyriode.runner.api;

import com.google.gson.Gson;
import fr.hyriode.runner.api.player.HyriRunnerPlayerManager;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class HyriRunnerApi {
    public static final Gson GSON = new Gson();
    private final HyriRunnerPlayerManager playerManager;

    public HyriRunnerApi() {
        this.playerManager = new HyriRunnerPlayerManager(this);
    }

    public HyriRunnerPlayerManager getPlayerManager() {
        return this.playerManager;
    }
}
