package cn.sandtripper.minecraft.sandbacklastserver;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class CoolDownManager {
    private final SandBackLastServer plugin;
    private int coolDown;
    private HashMap<String, Long> hashMap;
    private Queue<String> queue;

    public CoolDownManager(SandBackLastServer plugin) {
        this.plugin = plugin;
        this.hashMap = new HashMap<>();
        this.queue = new LinkedList<>();
        this.coolDown = plugin.getConfig().getInt("send-cool-down");
    }

    public void reload() {
        coolDown = plugin.getConfig().getInt("send-cool-down");
    }

    public void addExpire(String key) {
        long expireTime = System.currentTimeMillis() + coolDown * 1000L;
        hashMap.put(key, expireTime);
        queue.offer(key);
    }

    public boolean isExpire(String key) {
        updateQueue();
        return !hashMap.containsKey(key);
    }

    private void updateQueue() {
        long currentTime = System.currentTimeMillis();
        while (!queue.isEmpty()) {
            String firstKey = queue.peek();
            Long expireTime = hashMap.get(firstKey);
            if (expireTime == null || currentTime > expireTime) {
                queue.poll();
                hashMap.remove(firstKey);
            } else {
                break;
            }
        }
    }
}
