package cn.sandtripper.minecraft.sandbacklastserver;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandHandler implements CommandExecutor {

    private final SandBackLastServer plugin;


    public CommandHandler(SandBackLastServer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            if (!sender.hasPermission("sandbacklastserver.help")) {
                sender.sendMessage("§e你没有权限");
                return true;
            }
            sendHelp(sender);
        } else {
            if (args[0].equals("reload")) {
                if (!sender.hasPermission("sandbacklastserver.reload")) {
                    sender.sendMessage("§e你没有权限");
                    return true;
                }
                long lastTime = System.currentTimeMillis();
                this.plugin.reload();
                sender.sendMessage(String.format("§a插件重载成功,用时%dms", System.currentTimeMillis() - lastTime));
            } else if (args[0].equals("send")) {
                if (!plugin.isLobby) {
                    sender.sendMessage("§e该功能在is-lobby设置为false时不可用");
                    return true;
                }
                if (!sender.hasPermission("sandbacklastserver.send")) {
                    sender.sendMessage("§e你没有权限");
                    return true;
                }
                if (args.length == 1) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        plugin.lobbyManager.sendPlayer(player.getName());
                        sender.sendMessage("§a已进入查询队列！");
                    } else {
                        sender.sendMessage("§e需要以玩家身份执行");
                    }
                } else if (args.length == 2) {
                    Player player = plugin.getServer().getPlayer(args[1]);
                    if (player != null) {
                        plugin.lobbyManager.sendPlayer(args[1]);
                        sender.sendMessage("§a玩家已进入查询队列！");
                    } else {
                        sender.sendMessage("§e找不到该玩家");
                    }
                }
            } else if (args[0].equals("sendc")) {
                if (!sender.hasPermission("sandbacklastserver.sendc")) {
                    sender.sendMessage("§e你没有权限");
                    return true;
                }
                if (!plugin.isLobby) {
                    sender.sendMessage("§e该功能在is-lobby设置为false时不可用");
                    return true;
                }
                if (args.length == 1) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        plugin.lobbyManager.startPlayerCountdown(player.getName());
                        sender.sendMessage("§a已进入倒计时！");
                    } else {
                        sender.sendMessage("§e需要以玩家身份执行");
                    }
                } else if (args.length == 2) {
                    Player player = plugin.getServer().getPlayer(args[1]);
                    if (player != null) {
                        plugin.lobbyManager.startPlayerCountdown(args[1]);
                        sender.sendMessage("§a玩家已进入倒计时！");
                    } else {
                        sender.sendMessage("§e找不到该玩家");
                    }
                }
            } else if (args[0].equals("cancel")) {
                if (!sender.hasPermission("sandbacklastserver.cancel")) {
                    sender.sendMessage("§e你没有权限");
                    return true;
                }
                if (!plugin.isLobby) {
                    sender.sendMessage("§e该功能在is-lobby设置为false时不可用");
                    return true;
                }
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    plugin.lobbyManager.cancelPlayerCountdown(player.getName());
                    sender.sendMessage("§a取消成功！");
                } else {
                    sender.sendMessage("§e需要以玩家身份执行");
                }
            } else if (args[0].equals("help")) {
                if (!sender.hasPermission("sandbacklastserver.help")) {
                    sender.sendMessage("§e你没有权限");
                    return true;
                }
                sendHelp(sender);
            }
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6<----------SandBackLastServer 帮助---------->");
        sender.sendMessage("§e/sbls send (玩家) §6立即发送玩家到最近游玩的子服");
        sender.sendMessage("§e/sbls sendc (玩家) §6倒计时结束后，发送玩家到最近游玩的子服");
        sender.sendMessage("§e/sbls cancel §6取消倒计时");
        sender.sendMessage("§e/sbls reload §6重载插件，包括数据库连接");
    }

}