package com.chatsocketserverspigot;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.bukkit.Bukkit.getLogger;

public class SocketHandler {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private InputStream inputStream;
    private OutputStream outputStream;

    private MessageListener messageListener;

    private boolean isListening = false;
    private String token;

    public SocketHandler(String host, int port, String token) {
        this.token = token;
        try {
            InetAddress serverAddress = InetAddress.getByName(host);
            serverSocket = new ServerSocket(port, 0, serverAddress);
            getLogger().info("Socket服务器已启动在 sock://" + host + ":" + port + "\n");
        } catch (IOException e) {
            getLogger().severe("启动Socket服务器消息时发生异常：\n" + e.getMessage());
        }
        ExecutorService messageExecutor = Executors.newCachedThreadPool();
    }

    public void acceptConnection() {
        try {
            while (isListening) {
                serverSocket.setSoTimeout(1000); // 设置超时时间为1秒
                clientSocket = serverSocket.accept();
                if (clientSocket != null) {
                    inputStream = clientSocket.getInputStream();
                    outputStream = clientSocket.getOutputStream();
                    getLogger().info("客户端连接成功\n");

                    // 验证 token
                    if (verifyToken()) {
                        // 启动后台线程来监听客户端消息
                        startMessageListener();
                    } else {
                        getLogger().severe("Token 验证失败，关闭连接\n");
                        closeConnection();
                    }
                    break; // 成功连接后退出循环
                }
            }
        } catch (SocketTimeoutException e) {
            // 在超时时执行其他操作，例如日志记录或其他处理
        } catch (IOException e) {
            getLogger().severe("Socket客户端连接发生异常：\n" + e.getMessage());
        }
    }

    private boolean verifyToken() {
        try {
            byte[] buffer = new byte[4096]; // 设置合适的缓冲区大小
            int bytesRead = inputStream.read(buffer);
            String receivedToken = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8).trim();

            return token.equals(receivedToken);
        } catch (IOException e) {
            getLogger().severe("Token 验证时发生异常：\n" + e.getMessage());
            return false;
        }
    }

    public void sendData(String data) {
        try {
            outputStream.write(data.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            getLogger().severe("Socket发送到客户端时发生异常：\n" + e.getMessage());
        }
    }

    public void setClientMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }

    public void startListening() {
        isListening = true;
        while (isListening) {
            try {
                System.out.println("等待客户端连接...");
                clientSocket = serverSocket.accept();
                System.out.println("客户端连接成功\n");
                inputStream = clientSocket.getInputStream();
                outputStream = clientSocket.getOutputStream();

                // 验证 token
                if (verifyToken()) {
                    // 启动后台线程来监听客户端消息
                    startMessageListener();
                } else {
                    System.err.println("Token 验证失败，关闭连接\n");
                    closeConnection();
                }
            } catch (IOException e) {
                System.err.println("Socket客户端连接发生异常：\n" + e.getMessage());
            }
        }
    }

    public void stopListening() {
        isListening = false;
    }

    private void startMessageListener() {
        Runnable listenerTask = () -> {
            try {
                byte[] buffer = new byte[4096]; // 设置合适的缓冲区大小
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    if (messageListener != null) {
                        String receivedMessage = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                        messageListener.onMessageReceived(receivedMessage);
                    }
                }
                // 客户端断开连接，关闭资源
                closeConnection();
            } catch (IOException e) {
                getLogger().severe("Socket接收客户端消息时发生异常：\n" + e.getMessage());
            }
        };
        Thread messageListenerThread = new Thread(listenerTask);
        messageListenerThread.start();
    }

    public void closeConnection() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
            getLogger().info("客户端连接已关闭\n");
        } catch (IOException e) {
            getLogger().severe("Socket客户端关闭异常：\n" + e.getMessage());
        }
    }
}
