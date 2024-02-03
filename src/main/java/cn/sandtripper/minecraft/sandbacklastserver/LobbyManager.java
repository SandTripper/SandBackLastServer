package cn.sandtripper.minecraft.sandbacklastserver;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LobbyManager {
    private final SandBackLastServer plugin;
    private MessageManager messageManager;
    private CoolDownManager coolDownManager;

    private String defaultServer;
    private int sendCoolDown;
    private int sendCountdown;
    private MessageManager.MessageType sendCountdownReminderType;
    private String sendCountdownMessage;
    private MessageManager.MessageType sendReminderType;
    private String sendMessage;
    private String successMessage;
    private BlockingQueue<String> playerSendQueue;
    private BlockingQueue<PlayerLastServer> playerLastServer;
    private BukkitTask playerSendTask;
    private HashMap<String, BukkitTask> playerCoolDownTask;
    private volatile boolean isRunning;

    public LobbyManager(SandBackLastServer plugin) {
        this.plugin = plugin;
        this.isRunning = true;
        this.playerSendQueue = new LinkedBlockingQueue<>();
        this.playerLastServer = new LinkedBlockingQueue<>();
        this.playerCoolDownTask = new HashMap<>();
        this.messageManager = new MessageManager(plugin);
        this.coolDownManager = new CoolDownManager(plugin);

        startPlayerSendRunnable();

        processPlayerSendQueue();

        initTmp();
    }

    void reload() {
        coolDownManager.reload();
        initTmp();
    }

    void disable() {
        isRunning = false;
        if (playerSendTask != null) {
            playerSendTask.cancel();
        }
    }

    private void initTmp() {
        FileConfiguration config = plugin.getConfig();
        this.defaultServer = config.getString("default-server");
        this.sendCoolDown = config.getInt("send-cool-down");
        this.sendCountdown = config.getInt("countdown-config.countdown");
        this.sendCountdownReminderType = messageManager.getMessageType(config.getString("countdown-config.reminder-type"), MessageManager.MessageType.CHAT);
        this.sendCountdownMessage = colorFormat(config.getString("countdown-config.message"));
        this.sendReminderType = messageManager.getMessageType(config.getString("send-reminder-type"), MessageManager.MessageType.CHAT);
        this.sendMessage = colorFormat(config.getString("send-message"));
        this.successMessage = colorFormat(config.getString("success-message"));
    }

    public void startPlayerCountdown(String playerName) {
        if (playerCoolDownTask.containsKey(playerName)) {
            return;
        }
        Player player = plugin.getServer().getPlayer(playerName);
        BukkitTask task = new BukkitRunnable() {
            private int cnt = sendCountdown;

            @Override
            public void run() {
                if (cnt <= 0) {
                    sendPlayer(playerName);
                    this.cancel();
                }
                if (!player.isOnline()) {
                    this.cancel();
                }
                messageManager.sendPlayerMessage(player, sendCountdownReminderType, sendCountdownMessage.replace("{TIME}", String.valueOf(cnt)), 20);
                cnt--;
            }
        }.runTaskTimer(plugin, 0, 20L);
        playerCoolDownTask.put(playerName, task);
    }

    public void cancelPlayerCountdown(String playerName) {
        if (playerCoolDownTask.containsKey(playerName)) {
            playerCoolDownTask.get(playerName).cancel();
            playerCoolDownTask.remove(playerName);
        }
    }

    public void sendPlayer(String playerName) {
        if (playerCoolDownTask.containsKey(playerName)) {
            playerCoolDownTask.get(playerName).cancel();
            playerCoolDownTask.remove(playerName);
        }
        if (coolDownManager.isExpire(playerName)) {
            messageManager.sendPlayerMessage(plugin.getServer().getPlayer(playerName), sendReminderType, sendMessage, 60);
            playerSendQueue.offer(playerName);
            coolDownManager.addExpire(playerName);
        }
    }

    private void processPlayerSendQueue() {
        new Thread(() -> {
            while (isRunning) {
                String playerName = playerSendQueue.poll();
                if (playerName != null) {
                    String serverName = plugin.mySQLManager.getPlayerLast(playerName);
                    if (serverName != null) {
                        if (serverName.isEmpty()) {
                            serverName = defaultServer;
                        }
                        PlayerLastServer pls = new PlayerLastServer();
                        pls.playerName = playerName;
                        pls.serverName = serverName;
                        playerLastServer.offer(pls);
                    }
                } else {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }

    private void startPlayerSendRunnable() {
        playerSendTask = new BukkitRunnable() {
            @Override
            public void run() {
                while (true) {
                    PlayerLastServer pls = playerLastServer.poll();
                    if (pls != null) {
                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        out.writeUTF("Connect");
                        out.writeUTF(pls.serverName);  // 指定子服务器的名称
                        Player player = plugin.getServer().getPlayer(pls.playerName);
                        messageManager.sendPlayerMessage(player, sendReminderType, successMessage, 50);
                        // 发送插件消息到BungeeCord
                        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
                    } else {
                        break;
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20L);
    }

    private String colorFormat(String message) {
        if (message == null) {
            return "";
        }
        return message.replace("&", "§");
    }

    static class PlayerLastServer {
        String playerName;
        String serverName;
    }
}
