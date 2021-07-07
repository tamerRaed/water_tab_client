package com.tamer.alna99.watertabclient.model;

import com.tamer.alna99.watertabclient.view.NetworkUtils;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class MySocket {
    private static Socket instance;

    private MySocket() {
    }

    public static Socket getInstance() {
        if (instance == null) {
            try {
                instance = IO.socket(NetworkUtils.BASE_URL);
            } catch (URISyntaxException ignored) {
            }
        }
        return instance;
    }

}