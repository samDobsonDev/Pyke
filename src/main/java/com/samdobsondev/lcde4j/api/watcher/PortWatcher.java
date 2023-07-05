package com.samdobsondev.lcde4j.api.watcher;

import java.io.IOException;
import java.net.ServerSocket;

import java.net.InetSocketAddress;

public class PortWatcher {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 2999;

    public boolean isPortUp() {
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.setReuseAddress(false);
            serverSocket.bind(new InetSocketAddress(HOST, PORT), 1);
            return false;
        } catch (IOException e) {
            return true;
        }
    }
}
