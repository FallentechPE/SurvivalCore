package io.github.haappi.ftechcore;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public final class FTechCore extends JavaPlugin {
    public static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static FTechCore instance;
    private static JedisPool jedus;

    public static FTechCore getInstance() {
        return instance;
    }

    public static Jedis getJedisResource() {
        return jedus.getResource();
    }

    public static void returnJedisResource(Jedis connection) {
        jedus.returnResource(connection);
    }

    @Override
    public void onEnable() {
        instance = this;
        Config.getInstance();
        jedus = new JedisPool(Config.getInstance().getRedisHost(), 6379, Config.getInstance().getRedisUsername(), Config.getInstance().getRedisPassword());
        getServer().getPluginManager().registerEvents(new BetterMOTD(), this);
        getServer().getPluginManager().registerEvents(new BetterChat(), this);
        getServer().getPluginManager().registerEvents(new Advancements(), this);
        getServer().getCommandMap().register("uuid", new UUID());
//        getServer().getPluginManager().registerEvents(new InventoryViewer(), this);

    }

    @Override
    public void onDisable() {
        Config.saveMotd(BetterMOTD.motds);
    }
}
