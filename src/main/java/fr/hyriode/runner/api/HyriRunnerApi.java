package fr.hyriode.runner.api;

import com.google.gson.Gson;
import fr.hyriode.runner.api.player.HyriRunnerPlayerManager;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class HyriRunnerApi {

    public static final String REDIS_KEY = "therunner:";
    public static final Gson GSON = new Gson();

    private boolean running;

    private final HyriRunnerPlayerManager playerManager;

    private final LinkedBlockingQueue<Consumer<Jedis>> redisRequests;
    private final Thread redisRequestsThread;

    private final JedisPool jedisPool;

    public HyriRunnerApi(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        this.redisRequests = new LinkedBlockingQueue<>();
        this.redisRequestsThread = new Thread(() -> {
            while (running) {
                try {
                    final Consumer<Jedis> request = this.redisRequests.take();
                    try (final Jedis jedis = this.getRedisResource()) {
                        if (jedis != null) {
                            request.accept(jedis);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Runner API - Redis processor");
        this.playerManager = new HyriRunnerPlayerManager(this);
    }

    public void start() {
        this.running = true;
        this.redisRequestsThread.start();
    }

    public void stop() {
        this.running = false;
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.redisRequestsThread.interrupt();
    }

    public JedisPool getJedisPool() {
        return this.jedisPool;
    }

    public Jedis getRedisResource() {
        return this.jedisPool.getResource();
    }

    public void redisRequest(Consumer<Jedis> request) {
        this.redisRequests.add(request);
    }

    public String getFromRedis(String key) {
        try (final Jedis jedis = this.getRedisResource()) {
            if (jedis != null) {
                return jedis.get(key);
            }
        }
        return null;
    }

    public HyriRunnerPlayerManager getPlayerManager() {
        return this.playerManager;
    }
}
