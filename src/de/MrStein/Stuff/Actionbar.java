package de.MrStein.Stuff;

import de.MrStein.Boots;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class Actionbar {

    private static final Map<Player, BukkitTask> PENDING_MESSAGES = new HashMap<>();

    public static void sendActionBarMessage(Player player, String message) {
        sendRawActionBarMessage(player, "{\"text\":\"" + message + "\"}");
    }

    public static void sendRawActionBarMessage(Player p, String rawMessage) {
        try {
            Object chat = getClassbyName("ChatSerializer").getMethod("a", String.class).invoke(null, new Object[] { rawMessage });
            Object packet = getClassbyName("PacketPlayOutChat").getConstructor(getClassbyName("IChatBaseComponent"), Byte.TYPE).newInstance(new Object[] { chat, Byte.valueOf((byte) 2) });
            Object player = p.getClass().getMethod("getHandle").invoke(p);
            Object connection = player.getClass().getField("playerConnection").get(player);
            connection.getClass().getMethod("sendPacket", getClassbyName("Packet") ).invoke(connection, packet );
        } catch (Exception e) {}
    }

    public static void sendActionBarMessage(final Player player, final String message, final int duration, Plugin plugin) {
        cancelPendingMessages(player);
        final BukkitTask messageTask = new BukkitRunnable() {
            private int count = 0;

            public void run() {
                if (count >= (duration - 3)) {
                    this.cancel();
                }
                sendActionBarMessage(player, message);
                count++;
            }
        }.runTaskTimer(plugin, 0L, 20L);
        PENDING_MESSAGES.put(player, messageTask);
    }

    public static void cancelPendingMessages(Player player) {
        if (PENDING_MESSAGES.containsKey(player)) {
            PENDING_MESSAGES.get(player).cancel();
        }
    }

    private static Class<?> getClassbyName(String ClassName) throws ClassNotFoundException {
        if (ClassName.equals("ChatSerializer") && !Boots.version.equals("v1_8_R1")) {
            ClassName = "IChatBaseComponent$ChatSerializer";
        }
        return Class.forName("net.minecraft.server." + Boots.version + "." + ClassName);
    }

}
