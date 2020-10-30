package net.fabricmc.fabric.api.networking.v1;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.Future;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.networking.server.ServerLoginNetworkHandlerHook;
import net.fabricmc.fabric.impl.networking.server.ServerNetworkingImpl;
import net.fabricmc.fabric.mixin.networking.accessor.ServerLoginNetworkHandlerAccessor;

public class ServerLoginNetworking {
	/**
	 * Registers a handler to a channel.
	 *
	 * <p>If a handler is already registered to the {@code channel}, this method will return {@code false}, and no change will be made.
	 * Use {@link #unregister(Identifier)} to unregister the existing handler.</p>
	 *
	 * @param channel the id of the channel
	 * @param handler the handler
	 * @return false if a handler is already registered to the channel
	 */
	public static boolean register(Identifier channel, LoginChannelHandler handler) {
		Objects.requireNonNull(channel, "Channel cannot be null");
		Objects.requireNonNull(handler, "Login channel handler cannot be null");

		return ServerNetworkingImpl.LOGIN.register(channel, handler);
	}

	/**
	 * Removes the handler of a channel.
	 *
	 * <p>The {@code channel} is guaranteed not to have a handler after this call.</p>
	 *
	 * @param channel the id of the channel
	 * @return the previous handler, or {@code null} if no handler was bound to the channel
	 */
	@Nullable
	public static LoginChannelHandler unregister(Identifier channel) {
		Objects.requireNonNull(channel, "Channel cannot be null");

		return ServerNetworkingImpl.LOGIN.unregister(channel);
	}

	public static Collection<Identifier> getGlobalReceivers() {
		return ServerNetworkingImpl.LOGIN.getChannels();
	}

	public static boolean hasGlobalReceiver(Identifier channel) {
		return ServerNetworkingImpl.LOGIN.hasChannel(channel);
	}

	/**
	 * Returns the login query packet sender for a server login network handler.
	 *
	 * @param handler the server login network handler
	 * @return the associated login query packet sender
	 */
	public static PacketSender getLoginSender(ServerLoginNetworkHandler handler) {
		Objects.requireNonNull(handler, "Network handler cannot be null");

		return ((ServerLoginNetworkHandlerHook) handler).getAddon();
	}

	/**
	 * Returns the <i>Minecraft</i> Server of a server login network handler.
	 *
	 * @param handler the server login network handler
	 */
	public static MinecraftServer getServer(ServerLoginNetworkHandler handler) {
		Objects.requireNonNull(handler, "Network handler cannot be null");

		return ((ServerLoginNetworkHandlerAccessor) handler).getServer();
	}

	private ServerLoginNetworking() {
	}

	@FunctionalInterface
	public interface LoginChannelHandler {
		/**
		 * Handles an incoming query response from a client.
		 *
		 * <p>Whether the client understood the query should be checked before reading from the payload of the packet.
		 *
		 * @param handler the network handler that received this packet
		 * @param server the server
		 * @param sender the packet sender
		 * @param buf the payload of the packet
		 * @param understood whether the client
		 * @param synchronizer the synchronizer which may be used to delay log-in till a {@link Future} is completed.
		 */
		void receive(ServerLoginNetworkHandler handler, MinecraftServer server, PacketSender sender, PacketByteBuf buf, boolean understood, LoginSynchronizer synchronizer);
	}

	/**
	 * Allows blocking client log-in until all all futures passed into {@link LoginSynchronizer#waitFor(Future)} are done.
	 *
	 * @apiNote this interface is not intended to be implemented by users of api.
	 */
	@FunctionalInterface
	public interface LoginSynchronizer {
		/**
		 * Allows blocking client log-in until the {@code future} is {@link Future#isDone() done}.
		 *
		 * <p>Since packet reception happens on netty's event loops, this allows handlers to
		 * perform logic on the Server Thread, etc. For instance, a handler can prepare an
		 * upcoming query request or check necessary login data on the server thread.</p>
		 *
		 * <p>Here is an example where the player log-in is blocked so that a credential check and
		 * building of a followup query request can be performed properly on the logical server
		 * thread before the player successfully logs in:
		 * <pre>{@code
		 * ServerLoginNetworking.getLoginReceivers().register(CHECK_CHANNEL, (handler, server, sender, buf, understood, synchronizer) -&gt; {
		 * 	if (!understood) {
		 * 		handler.disconnect(new LiteralText("Only accept clients that can check!"));
		 * 		return;
		 * 	}
		 *
		 * 	String checkMessage = buf.readString(32767);
		 *
		 * 	// Just send the CompletableFuture returned by the server's submit method
		 * 	synchronizer.waitFor(server.submit(() -&gt; {
		 * 		LoginInfoChecker checker = LoginInfoChecker.get(server);
		 *
		 * 		if (!checker.check(handler.getConnectionInfo(), checkMessage)) {
		 * 			handler.disconnect(new LiteralText("Invalid credentials!"));
		 * 			return;
		 * 		}
		 *
		 * 		sender.send(UPCOMING_CHECK, checker.buildSecondQueryPacket(handler, checkMessage));
		 * 	}));
		 * });
		 * }</pre>
		 * Usually it is enough to pass the return value for {@link net.minecraft.util.thread.ThreadExecutor#submit(Runnable)}
		 * for {@code future}.</p>
		 *
		 * @param future the future that must be done before the player can log in
		 */
		void waitFor(Future<?> future);
	}
}
