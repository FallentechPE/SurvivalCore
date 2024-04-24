package io.github.haappi.ftechcore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.jetbrains.annotations.Nullable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class BetterMOTD implements Listener {
    public static final HashMap<String, String> motds = new HashMap<>();

    private static @Nullable String calculateSHA1(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = digest.digest(input.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerListPing(ServerListPingEvent event) {
        String sha = calculateSHA1(event.getAddress().getHostAddress());
        event.motd(Config.getInstance().getMotd());
        if (sha == null) {
            return;
        }
        String username = motds.get(sha);
        if (username == null) {
            return;
        }
        event.motd(Component.text("Welcome back, ", NamedTextColor.GREEN).append(Component.text(username, NamedTextColor.YELLOW)).append(Component.text("!", NamedTextColor.GREEN)).appendNewline().append(Config.getInstance().getMotd()));
        motds.put(sha, username);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        try {
            String sha = calculateSHA1(event.getPlayer().getAddress().getAddress().getHostAddress());
            if (sha == null) {
                return;
            }
            motds.put(sha, event.getPlayer().getName());
            Config.saveMotd(motds);
        } catch (Exception e) {
            System.out.println("womp womp, unable to get the address of " + event.getPlayer().getName() + " :(");
        }
    }
}
