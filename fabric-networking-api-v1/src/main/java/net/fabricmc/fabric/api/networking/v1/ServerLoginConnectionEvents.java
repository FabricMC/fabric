package net.fabricmc.fabric.api.networking.v1;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ServerLoginConnectionEvents {
	/**
	 * An event for the start of login queries of the server login network handler.
	 *
	 * <p>Use {@link ServerLoginNetworking#getLoginSender(ServerLoginNetworkHandler)} to obtain the query request packet sender in the callback.
	 */
	public static final Event<LoginQueryStart> LOGIN_QUERY_START = EventFactory.createArrayBacked(LoginQueryStart.class, callbacks -> (handler, server, sender, synchronizer) -> {
		for (LoginQueryStart callback : callbacks) {
			callback.onLoginStart(handler, server, sender, synchronizer);
		}
	});
	/**
	 * An event for the disconnection of the server login network handler.
	 *
	 * <p>No packets should be sent when this event is invoked.
	 */
	public static final Event<LoginDisconnect> LOGIN_DISCONNECTED = EventFactory.createArrayBacked(LoginDisconnect.class, callbacks -> (handler, server) -> {
		for (LoginDisconnect callback : callbacks) {
			callback.onLoginDisconnected(handler, server);
		}
	});

	private ServerLoginConnectionEvents() {
	}

	@FunctionalInterface
	public interface LoginQueryStart {
		void onLoginStart(ServerLoginNetworkHandler handler, MinecraftServer server, PacketSender sender, ServerLoginNetworking.LoginSynchronizer synchronizer);
	}

	@FunctionalInterface
	public interface LoginDisconnect {
		void onLoginDisconnected(ServerLoginNetworkHandler handler, MinecraftServer server);
	}
}
