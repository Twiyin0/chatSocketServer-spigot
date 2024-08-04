package com.chatsocketserverspigot;

import static org.bukkit.Bukkit.*;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.clip.placeholderapi.PlaceholderAPI;

public final class ChatSocketServer_spigot extends JavaPlugin implements Listener {

    private SocketHandler socketHandler;
    private File configFile;
    private FileConfiguration config;
    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
        chatSocketCmd commandExecutor = new chatSocketCmd(this); // 将插件实例传递给命令执行器
        getPluginCommand("chatsocketserver").setExecutor(new chatSocketCmd(this));
        // 创建或加载配置文件
        configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            // 如果配置文件不存在，设置默认值并保存
            config = YamlConfiguration.loadConfiguration(configFile);
            config.set("host", "0.0.0.0");
            config.set("port", 21354);
            config.set("token", "Token12345");
            config.set("CMDprefix", "[socketReceived] >> ");
            config.set("CHATprefix", "§6[Socket消息]§r");
            saveConfig();
        } else {
            // 配置文件存在，加载它
            config = YamlConfiguration.loadConfiguration(configFile);
        }
        getLogger().info("插件启动成功!\n");
        // 初始化 socketHandler
        String sockethost =config.getString("host");
        int socketPort = config.getInt("port");
        String token = config.getString("token");
        socketHandler = new SocketHandler(sockethost, socketPort, token); // 指定主机和端口与token
        String receivedCMDPrefix = config.getString("CMDprefix");
        String receivedCHATPrefix = config.getString("CHATprefix");

        socketHandler.acceptConnection();
        // 设置消息监听器
        socketHandler.setClientMessageListener(message -> {
            // 在这里处理接收到的消息
            getLogger().info(receivedCMDPrefix + message);
            if (message.equals("TPSn\n") || message.equals("tps\n")) {
                // 使用异步任务发送聊天消息到Socket服务器
                String papivar = "%server_tps%";
                String dataToSend = PlaceholderAPI.setPlaceholders(null, "服务器前1分钟, 5分钟, 15分钟的TPS分为为: "+papivar);
                Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                    try {
                        // 将消息从UTF-8编码转换为utf8编码
                        byte[] u8Bytes = new byte[0];
                        String u8msg="";
                        u8Bytes = dataToSend.getBytes(StandardCharsets.UTF_8);
                        u8msg = new String(u8Bytes, StandardCharsets.UTF_8);
                        // 将聊天内容发送到Socket客户端
                        socketHandler.sendData(u8msg);
                    } catch (Exception e) {
                        // 处理异常，可以记录错误信息并采取适当的措施
                        getLogger().severe("发送聊天消息到Socket服务器时发生异常：" + e.getMessage());
                    }
                });
            }
            if (message.equals("服务器信息\n") || message.equals("server_info\n")) {
                // 使用异步任务发送聊天消息到Socket服务器
                String papivar = "版本: %server_version%\n在线人数: %server_online%/%server_max_players%\ntps: %server_tps%";
                String dataToSend = PlaceholderAPI.setPlaceholders(null, "服务器信息\n"+papivar);
                Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                    try {
                        // 将消息从UTF-8编码转换为GBK编码
                        byte[] u8Bytes = new byte[0];
                        String u8msg="";
                        u8Bytes = dataToSend.getBytes(StandardCharsets.UTF_8);
                        u8msg = new String(u8Bytes, StandardCharsets.UTF_8);
                        // 将聊天内容发送到Socket客户端
                        socketHandler.sendData(u8msg);
                    } catch (Exception e) {
                        // 处理异常，可以记录错误信息并采取适当的措施
                        getLogger().severe("发送聊天消息到Socket服务器时发生异常：" + e.getMessage());
                    }
                });
            }
            else {
                String papivar = PlaceholderAPI.setPlaceholders(null, message);
                // 可以将消息广播给服务器内的玩家
                getServer().broadcastMessage(receivedCHATPrefix + '\n' + papivar);
            }
        });
        // 启动监听客户端连接的线程
        Thread listenerThread = new Thread(() -> {
            socketHandler.startListening();
        });
        listenerThread.start();
    }

    @Override
    public void onDisable() {
        socketHandler.closeConnection();
        getLogger().info("插件已关闭!\n");
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
//        String playerName = event.getPlayer().getName();
        String message = event.getMessage();
        String dataToSend = "[聊天信息]>> %player_displayname%: " + message;
        String papivar = PlaceholderAPI.setPlaceholders(event.getPlayer(), dataToSend);

        // 使用异步任务发送聊天消息到Socket服务器
        getScheduler().runTaskAsynchronously(this, () -> {
            try {
                // 将消息从UTF-8编码转换为u8编码
                byte[] u8Bytes = new byte[0];
                String u8msg="";
                u8Bytes = papivar.getBytes(StandardCharsets.UTF_8);
                u8msg = new String(u8Bytes, StandardCharsets.UTF_8);
                // 将聊天内容发送到Socket客户端
                socketHandler.sendData(u8msg);
            } catch (Exception e) {
                // 处理异常，可以记录错误信息并采取适当的措施
                getLogger().severe("发送聊天消息到Socket服务器时发生异常：" + e.getMessage());
            }
        });
    }

//    // paper端的聊天监听
//    @EventHandler
//    public void onPaperPlayerChat(AsyncChatEvent event) {
//        String playerName = event.getPlayer().getName();
//        String message = event.message().toString();
//        Pattern pattern = Pattern.compile("content=\"(.*?)\"");
//        Matcher matcher = pattern.matcher(message);
//        if (matcher.find()) {
//            String msg = matcher.group(1);
//            String dataToSend = "[聊天信息]>> "+playerName + ": " + msg;
//            // 使用异步任务发送聊天消息到Socket服务器
//            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
//                try {
//                    // 将消息从UTF-8编码转换为u8编码
//                    byte[] u8Bytes = new byte[0];
//                    String u8msg="";
//                    u8Bytes = dataToSend.getBytes(StandardCharsets.UTF_8);
//                    u8msg = new String(u8Bytes, StandardCharsets.UTF_8);
//                    // 将聊天内容发送到Socket客户端
//                    socketHandler.sendData(u8msg);
//                } catch (Exception e) {
//                    // 处理异常，可以记录错误信息并采取适当的措施
//                    getLogger().severe("发送聊天消息到Socket服务器时发生异常：" + e.getMessage());
//                }
//            });
//        }
//    }

    // 监听死亡信息
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        String message = event.getDeathMessage();
        String papivar;
        if (message != null) {
            papivar = PlaceholderAPI.setPlaceholders(event.getEntity().getPlayer(), message);
        } else {
            papivar = null;
        }
        // 使用异步任务发送聊天消息到Socket服务器
        getScheduler().runTaskAsynchronously(this, () -> {
            try {
                byte[] u8Bytes = new byte[0];
                String msg="";
                if (papivar != null) {
                    u8Bytes = papivar.getBytes(StandardCharsets.UTF_8);
                    msg = new String(u8Bytes, StandardCharsets.UTF_8);
                }
                socketHandler.sendData("[死亡信息]>> " + msg);
            } catch (Exception e) {
                // 处理异常，可以记录错误信息并采取适当的措施
                getLogger().severe("发送聊天消息到Socket服务器时发生异常：" + e.getMessage());
            }
        });
    }

    // 玩家加入信息
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
//        String playerName = event.getPlayer().getName();
        String message = "%player_displayname% 加入服务器";
        String papivar = PlaceholderAPI.setPlaceholders(event.getPlayer(), message);
        // 使用异步任务发送聊天消息到Socket服务器
        getScheduler().runTaskAsynchronously(this, () -> {
            try {
                byte[] u8Bytes = new byte[0];
                String msg="";
                u8Bytes = papivar.getBytes(StandardCharsets.UTF_8);
                msg = new String(u8Bytes, StandardCharsets.UTF_8);
                socketHandler.sendData("[加入信息]>> " + msg);
            } catch (Exception e) {
                // 处理异常，可以记录错误信息并采取适当的措施
                getLogger().severe("发送聊天消息到Socket服务器时发生异常：" + e.getMessage());
            }
        });
    }

    // 玩家退出信息
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String playerName = event.getPlayer().getName();
        String message = "%player_displayname% 退出服务器";
        String papivar = PlaceholderAPI.setPlaceholders(event.getPlayer(), message);
        // 使用异步任务发送聊天消息到Socket服务器
        getScheduler().runTaskAsynchronously(this, () -> {
            try {
                byte[] u8Bytes = new byte[0];
                String msg="";
                u8Bytes = papivar.getBytes(StandardCharsets.UTF_8);
                msg = new String(u8Bytes, StandardCharsets.UTF_8);
                socketHandler.sendData("[退出信息]>> " + msg);
            } catch (Exception e) {
                // 处理异常，可以记录错误信息并采取适当的措施
                getLogger().severe("发送聊天消息到Socket服务器时发生异常：" + e.getMessage());
            }
        });
    }
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            getLogger().warning("无法保存配置文件: " + e.getMessage());
        }
    }
}
