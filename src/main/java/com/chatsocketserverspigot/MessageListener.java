package com.chatsocketserverspigot;

import java.io.UnsupportedEncodingException;

public interface MessageListener {
    void onMessageReceived(String message) throws UnsupportedEncodingException;
}
