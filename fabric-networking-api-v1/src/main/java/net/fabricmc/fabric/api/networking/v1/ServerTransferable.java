package net.fabricmc.fabric.api.networking.v1;

public interface ServerTransferable {

    void transferToServer(String host, int port);

    boolean wasTransferred();
}
