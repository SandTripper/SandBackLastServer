package cn.sandtripper.minecraft.sandbacklastserver;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyTabCompleter implements TabCompleter {
    private SandBackLastServer plugin;

    public MyTabCompleter(SandBackLastServer plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        // 在这里添加逻辑来决定哪些建议应该返回
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            suggestions = Arrays.asList("send", "sendc", "cancel", "reload", "help");
        } else if (args[0].equals("send")) {
            if (args.length == 2) {
                List<String> onlinePlayers = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    onlinePlayers.add(player.getName());
                }
                suggestions = onlinePlayers;
            }
        } else if (args[0].equals("sendc")) {
            if (args.length == 2) {
                List<String> onlinePlayers = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    onlinePlayers.add(player.getName());
                }
                suggestions = onlinePlayers;
            }
        }
        return suggestions;
    }
}