package cn.sandtripper.minecraft.sandbacklastserver;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import static cn.sandtripper.minecraft.sandbacklastserver.MessageManager.MessageType.*;

public class MessageManager {
    private final SandBackLastServer plugin;

    public MessageManager(SandBackLastServer plugin) {
        this.plugin = plugin;
    }

    MessageType getMessageType(String type, MessageType defaultType) {
        switch (type) {
            case "BOSSBAR":
                return BOSSBAR;
            case "CHAT":
                return CHAT;
            case "ACTIONBAR":
                return ACTIONBAR;
            case "TITLE":
                return TITLE;
        }
        return defaultType;
    }

    void sendPlayerMessage(Player player, MessageType type, String message, int ticks) {
        switch (type) {
            case BOSSBAR:
                BossBar bossBar = Bukkit.createBossBar(message, BarColor.BLUE, BarStyle.SOLID);
                bossBar.addPlayer(player);
                Bukkit.getScheduler().runTaskLater(plugin, bossBar::removeAll, ticks);
                break;
            case CHAT:
                player.sendMessage(message);
                break;
            case ACTIONBAR:
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
                break;
            case TITLE:
                player.sendTitle("", message, 10, ticks, 10);
                break;
        }
    }

    enum MessageType {
        BOSSBAR,
        CHAT,
        ACTIONBAR,
        TITLE
    }

}
