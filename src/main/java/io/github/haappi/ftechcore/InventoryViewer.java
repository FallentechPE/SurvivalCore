//package io.github.haappi.ftechcore;
//
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.EventPriority;
//import org.bukkit.event.Listener;
//import org.bukkit.event.player.PlayerQuitEvent;
//import org.bukkit.inventory.Inventory;
//import org.bukkit.inventory.ItemStack;
//
//import java.nio.ByteBuffer;
//import java.util.HashMap;
//
//public class InventoryViewer implements Listener {
//
//    public static byte[] serialize(Inventory inventory) {
//        ItemStack[] itemStacks = inventory.getContents();
//        return serialize(itemStacks);
//    }
//
//    public static byte[] serialize(ItemStack[] itemStacks) {
//        HashMap<ItemStack, byte[]> lePain = new HashMap<>();
//        int totalSize = Integer.BYTES;
//        for (ItemStack itemStack : itemStacks) {
//            if (itemStack != null) {
//                lePain.put(itemStack, itemStack.serializeAsBytes());
//                totalSize += Integer.BYTES + lePain.get(itemStack).length;
//            } else {
//                totalSize += Integer.BYTES;
//            }
//
//        }
//        ByteBuffer buffer = ByteBuffer.allocate(totalSize).putInt(itemStacks.length);
//        for (ItemStack itemStack : itemStacks) {
//            if (itemStack != null) buffer.putInt(lePain.get(itemStack).length).put(lePain.get(itemStack));
//            else buffer.putInt(0);
//        }
//        return buffer.array();
//    }
//
//    @EventHandler(priority = EventPriority.MONITOR)
//    public void onQuit(PlayerQuitEvent event) {
//        byte[] inventory = serialize(event.getPlayer().getInventory());
//        byte[] enderChest = serialize(event.getPlayer().getEnderChest());
//
//
//        FTechCore.getInstance().getServer().getAsyncScheduler().runNow(FTechCore.getInstance(), (scheduledTask) -> {
//            FTechCore.getJedisResource().set(("fallentech-" + event.getPlayer().getName() + "-inventory").getBytes(), inventory);
//            FTechCore.getJedisResource().set(("fallentech-" + event.getPlayer().getName() + "-enderchest").getBytes(), enderChest);
//        });
//    }
//}
