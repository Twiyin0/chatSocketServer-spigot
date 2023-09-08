package com.chatsocketserverspigot;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class chatSocketCmd implements CommandExecutor {
    private final ChatSocketServer_spigot plugin;

    public chatSocketCmd(ChatSocketServer_spigot plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (sender.hasPermission("chatSocketServer.admin")) {
            if (command.getName().equalsIgnoreCase("chatsocketserver")) {
                if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                    // 重启插件
                    plugin.getServer().getPluginManager().disablePlugin(plugin);
                    plugin.getServer().getPluginManager().enablePlugin(plugin);
                    sender.sendMessage("ChatSocketServer插件已重启！");
                    return true;
                }
            }
            if (command.getName().equalsIgnoreCase("chatsocketserver")) {
                if (args.length == 1 && args[0].equalsIgnoreCase("papiTest1")) {
                    Player player = (Player)sender;
                    String variable="%player_name%";
                    // 使用PlaceholderAPI解析变量
                    String result = PlaceholderAPI.setPlaceholders(player.getPlayer(), "你好"+variable);
                    sender.sendMessage(result);
                    return true;
                }
                if (args.length == 1 && args[0].equalsIgnoreCase("papiTest2")) {
                    Player player = (Player)sender;
                    String variable="%server_tps%";
                    // 使用PlaceholderAPI解析变量
                    String result = PlaceholderAPI.setPlaceholders(null, "TPS: "+variable);
                    sender.sendMessage(result);
                    return true;
                }
            }
        }
        else {
                Bukkit.broadcastMessage("你没有权限使用!\n");
        }
        return false;
    }
}
