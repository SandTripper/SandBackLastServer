package cn.sandtripper.minecraft.sandbacklastserver;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    private SandBackLastServer plugin;

    PlayerListener(SandBackLastServer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();
        if (!plugin.isLobby) {
            plugin.unLobbyManager.handlePlayerJoin(playerName);
        }
    }

}

