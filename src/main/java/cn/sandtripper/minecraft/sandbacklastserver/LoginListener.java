package cn.sandtripper.minecraft.sandbacklastserver;

import fr.xephi.authme.events.LoginEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public class LoginListener implements Listener {
    private SandBackLastServer plugin;

    LoginListener(SandBackLastServer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLogin(LoginEvent event) {
        if (plugin.isSendWhenLogin) {
            plugin.lobbyManager.startPlayerCountdown(event.getPlayer().getName());
        }
    }
}
