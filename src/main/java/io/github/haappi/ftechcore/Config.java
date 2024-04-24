package io.github.haappi.ftechcore;

import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

import static io.github.haappi.ftechcore.BetterMOTD.motds;

public class Config {
    private static Config instance;
    private final Component motd;
    private final String redisUsername;
    private final String redisPassword;
    private final String redisHost;

    private Config(FTechCore plugin) {
        instance = this;
        plugin.getConfig().addDefault("motd", "<aqua>Fallen<gold>Tech <light_purple>Survival Server");
        plugin.getConfig().addDefault("redis.username", "default");
        plugin.getConfig().addDefault("redis.password", "password");
        plugin.getConfig().addDefault("redis.host", "localhost");
        motd = FTechCore.MINI_MESSAGE.deserialize(plugin.getConfig().getString("motd") == null ? "<aqua>Fallen<gold>Tech <light_purple>Survival Server" : plugin.getConfig().getString("motd"));

        redisUsername = plugin.getConfig().getString("redis.username");
        redisPassword = plugin.getConfig().getString("redis.password");
        redisHost = plugin.getConfig().getString("redis.host");

        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();

        ConfigurationSection motdsSection = plugin.getConfig().getConfigurationSection("motds");
        if (motdsSection != null) {
            for (String key : motdsSection.getKeys(false)) {
                motds.put(key, motdsSection.getString(key));
            }
        }
    }

    public static void saveMotd(HashMap<String, String> motds) {
        for (String key : motds.keySet()) {
            FTechCore.getInstance().getConfig().set("motds." + key, motds.get(key));
        }
        FTechCore.getInstance().saveConfig();
    }

    public static Config getInstance() {
        if (instance == null) {
            new Config(FTechCore.getInstance());
        }
        return instance;
    }

    public static Config reload() {
        return new Config(FTechCore.getInstance());
    }

    public Component getMotd() {
        return motd;
    }

    public String getRedisUsername() {
        return redisUsername;
    }

    public String getRedisPassword() {
        return redisPassword;
    }

    public String getRedisHost() {
        return redisHost;
    }
}
