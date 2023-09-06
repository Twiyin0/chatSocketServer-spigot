package com.chatsocketserverspigot;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
        }
        else {
                Bukkit.broadcastMessage("你没有权限使用!\n");
        }
        return false;
    }
}
