package net.fabricmc.fabric.api.networking.v1;

import java.net.InetSocketAddress;
import java.net.URL;

public interface ServerTransferable {

    void transferToServer(String host, int port);

    default void transferToServer(URL address) {
        transferToServer(address.getHost(), address.getPort());
    }

    default void transferToServer(InetSocketAddress address) {
        transferToServer(address.getHostName(), address.getPort());
    }

    boolean wasTransferred();
}
