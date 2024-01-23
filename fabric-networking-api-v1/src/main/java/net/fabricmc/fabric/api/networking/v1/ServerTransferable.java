package net.fabricmc.fabric.api.networking.v1;

public interface ServerTransferable {

	/**
	 * Sends the client to a different server. Can also be used on the current server
	 *
	 * @param host The hostname or ip address of the server to transfer to
	 * @param port The port of the server to transfer to
	 */
    void transferToServer(String host, int port);

	/**
	 * @return Whether or not this client joined from a server transfer
	 */
    boolean wasTransferred();
}
