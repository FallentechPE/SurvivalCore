package io.github.haappi.ftechcore;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import static net.kyori.adventure.text.Component.text;

public class BetterChat implements Listener {
    public BetterChat() {
        FTechCore.getInstance().getServer().getAsyncScheduler().runNow(FTechCore.getInstance(), task -> {
            FTechCore.getInstance().getLogger().info("Subscribing to chat channel");
            FTechCore.getJedisResource().subscribe(new PubSub(), "fallentech-chat");
        });
    }

    private static String formatEnchantName(String name) {
        return titleCase(name.replace("_", " ").toLowerCase());
    }

    private static String titleCase(String input) {
        StringBuilder titleCase = new StringBuilder(input.length());
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }

            titleCase.append(c);
        }

        return titleCase.toString();
    }

    private static String intToRoman(int num) {
        String[] M = {"", "M", "MM", "MMM"};
        String[] C = {"", "C", "CC", "CCC", "CD", "D",
                "DC", "DCC", "DCCC", "CM"};
        String[] X = {"", "X", "XX", "XXX", "XL", "L",
                "LX", "LXX", "LXXX", "XC"};
        String[] I = {"", "I", "II", "III", "IV", "V",
                "VI", "VII", "VIII", "IX"};

        return M[num / 1000] + C[(num % 1000) / 100] +
                X[(num % 100) / 10] + I[num % 10];
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event) {
        TextColor color = event.getPlayer().isOp() ? NamedTextColor.GREEN : event.getPlayer().getName().equals("haappi") ? TextColor.fromHexString("#c89eff") : NamedTextColor.YELLOW;
        Component before = event.getPlayer().getName().equals("haappi") ? text("(so kawaiii) ", color) : text("");
        event.renderer(ChatRenderer.viewerUnaware((source, sourceDisplayName, message) -> {
            return before.append(text("[", NamedTextColor.GRAY).append(sourceDisplayName.color(color)).append(text("]", NamedTextColor.GRAY)).append(text(" ").append(message.color(NamedTextColor.WHITE))));
        }));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void item(AsyncChatEvent event) {
        if (!PlainTextComponentSerializer.plainText().serialize(event.message()).equalsIgnoreCase("[item]")) {
            return;
        }

        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            return;
        }

        TextComponent.Builder builder = text();

        for (Enchantment enchantment : item.getEnchantments().keySet()) {
            builder.appendNewline().append(text(" - " + formatEnchantName(enchantment.getKey().getKey()) + " " + intToRoman(item.getEnchantmentLevel(enchantment)), NamedTextColor.GRAY))
                    .append(text(" "));
        }

        Component component = text("").append(item.displayName())
                .append(text(" x" + item.getAmount(), NamedTextColor.GRAY))
                .append(builder.build()).hoverEvent(item.asHoverEvent());

        event.message(component);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        TextColor color = event.getPlayer().isOp() ? NamedTextColor.GREEN : event.getPlayer().getName().equals("haappi") ? TextColor.fromHexString("#c89eff") : NamedTextColor.YELLOW;
        event.joinMessage(text("+ ", NamedTextColor.GREEN).append(event.getPlayer().displayName().color(color)));
        FTechCore.getInstance().getServer().getAsyncScheduler().runNow(FTechCore.getInstance(), task -> {
            Jedis resource = FTechCore.getJedisResource();
            resource.publish("fallentech-chat", "join;" + event.getPlayer().getName());
            FTechCore.returnJedisResource(resource);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        TextColor color = event.getPlayer().isOp() ? NamedTextColor.GREEN : event.getPlayer().getName().equals("haappi") ? TextColor.fromHexString("#c89eff") : NamedTextColor.YELLOW;
        event.quitMessage(text("- ", NamedTextColor.RED).append(event.getPlayer().displayName().color(color)));
        FTechCore.getInstance().getServer().getAsyncScheduler().runNow(FTechCore.getInstance(), task -> {
            Jedis resource = FTechCore.getJedisResource();
            resource.publish("fallentech-chat", "quit;" + event.getPlayer().getName());
            FTechCore.returnJedisResource(resource);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        FTechCore.getInstance().getServer().getAsyncScheduler().runNow(FTechCore.getInstance(), task -> {
            Jedis resource = FTechCore.getJedisResource();
            resource.publish("fallentech-chat", "death;" + event.getPlayer().getName() + " " + PlainTextComponentSerializer.plainText().serialize(event.deathMessage()));
            FTechCore.returnJedisResource(resource);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void broadcastToDiscord(AsyncChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        String message = "message;" + PlainTextComponentSerializer.plainText().serialize(event.getPlayer().displayName().appendSpace().append(event.message()));

        Jedis resource = FTechCore.getJedisResource();
        resource.publish("fallentech-chat", message);
        FTechCore.returnJedisResource(resource);
    }

    static private class PubSub extends JedisPubSub {
        public void onMessage(String channel, String message) {
            System.out.println("Received message: " + message);
            if (!message.contains(";") || message.split(";", 2).length != 2) {
                System.out.println("Invalid message: " + message);
            }

            String type = message.split(";", 2)[0];
            String content = message.split(";", 2)[1];

            switch (type) {
                case "server" -> {
                    Component parsed = FTechCore.MINI_MESSAGE.deserialize(content);
                    Bukkit.broadcast(parsed);
                }
                case "command" ->
                        Bukkit.getScheduler().runTask(FTechCore.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), content));
            }
        }
    }
}
