package cn.sandtripper.minecraft.sandbacklastserver;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UnLobbyManager {
    private final SandBackLastServer plugin;

    private BlockingQueue<String> playerJoinQueue;
    private String serverName;

    private volatile boolean isRunning;

    public UnLobbyManager(SandBackLastServer plugin) {
        this.isRunning = true;
        this.plugin = plugin;
        this.playerJoinQueue = new LinkedBlockingQueue<>();
        processPlayerJoinQueue();
        initTmp();
    }

    void reload() {
        initTmp();
    }

    void disable() {
        isRunning = false;
    }


    private void initTmp() {

        this.serverName = plugin.getConfig().getString("server-name");
    }

    void handlePlayerJoin(String playerName) {
        playerJoinQueue.offer(playerName);
    }

    private void processPlayerJoinQueue() {
        new Thread(() -> {
            while (isRunning) {
                String playerName = playerJoinQueue.poll();
                if (playerName != null) {
                    plugin.mySQLManager.savePlayerLast(playerName, serverName);
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
}
