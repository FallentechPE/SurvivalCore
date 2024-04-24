package io.github.haappi.ftechcore;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import redis.clients.jedis.Jedis;

public class Advancements implements Listener {

    private static void sendToDiscord(String message) {
        FTechCore.getInstance().getServer().getAsyncScheduler().runNow(FTechCore.getInstance(), task -> {
            Jedis resource = FTechCore.getJedisResource();
            resource.publish("fallentech-chat", "advancement;" + message);
            FTechCore.returnJedisResource(resource);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        if (event.getAdvancement().getKey().getKey().startsWith("recipes/")) {
            return;
        }
        if (event.getAdvancement().getDisplay() == null) {
            return;
        }
        String message = event.getPlayer().getName() +
                " has made the advancement " +
                PlainTextComponentSerializer.plainText().serialize(event.getAdvancement().displayName()) + "\\n  \\- " +
                PlainTextComponentSerializer.plainText().serialize(event.getAdvancement().getDisplay().description());

        sendToDiscord(message);
    }
}
