/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.api.networking.v1;

import java.util.concurrent.Future;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.networking.server.ServerLoginNetworkHandlerHook;
import net.fabricmc.fabric.impl.networking.server.ServerNetworkingDetails;
import net.fabricmc.fabric.impl.networking.server.ServerPlayNetworkHandlerHook;
import net.fabricmc.fabric.mixin.networking.accessor.ServerLoginNetworkHandlerAccessor;

/**
 * Offers access to server-side networking functionalities.
 *
 * <p>Server-side networking functionalities include receiving serverbound packets,
 * sending clientbound packets, and events related to server-side network handlers.</p>
 *
 * <p>This class should be only used for the logical server.</p>
 *
 * @see ClientNetworking
 */
public final class ServerNetworking {
	/**
	 * Returns the packet receiver for channel handler registration on server play network
	 * handlers, receiving {@link net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket
	 * client to server custom payload packets}.
	 */
	public static ChannelHandlerRegistry<PlayChannelHandler> getPlayReceiver() {
		return ServerNetworkingDetails.PLAY;
	}

	/**
	 * Returns the packet receiver for channel handler registration on server play network
	 * handlers, receiving {@link net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket
	 * login query response packets}.
	 */
	public static ChannelHandlerRegistry<LoginChannelHandler> getLoginReceiver() {
		return ServerNetworkingDetails.LOGIN;
	}

	/**
	 * Returns the packet sender for a server play network handler.
	 *
	 * @param handler a server play network handler
	 * @return the associated packet sender
	 */
	public static PlayPacketSender getPlaySender(ServerPlayNetworkHandler handler) {
		return ((ServerPlayNetworkHandlerHook) handler).getAddon();
	}

	/**
	 * Returns the login query packet sender for a server login network handler.
	 *
	 * @param handler the server login network handler
	 * @return the associated login query packet sender
	 */
	public static PacketSender getLoginSender(ServerLoginNetworkHandler handler) {
		return ((ServerLoginNetworkHandlerHook) handler).getAddon();
	}

	/**
	 * Returns the packet sender for a server player.
	 *
	 * <p>This is a shortcut for {@link #getPlaySender(ServerPlayNetworkHandler)}.</p>
	 *
	 * @param player a server player
	 * @return the associated packet sender
	 */
	public static PlayPacketSender getPlaySender(ServerPlayerEntity player) {
		return getPlaySender(player.networkHandler);
	}

	/**
	 * Returns the <i>Minecraft</i> Server of a server play network handler.
	 *
	 * @param handler the server play network handler
	 */
	public static MinecraftServer getServer(ServerPlayNetworkHandler handler) {
		return handler.player.server;
	}

	/**
	 * Returns the <i>Minecraft</i> Server of a server login network handler.
	 *
	 * @param handler the server login network handler
	 */
	public static MinecraftServer getServer(ServerLoginNetworkHandler handler) {
		return ((ServerLoginNetworkHandlerAccessor) handler).getServer();
	}

	/**
	 * Sends a packet to a player.
	 *
	 * @param player the player to send the packet to
	 * @param channel the channel of the packet
	 * @param buf the payload of the packet.
	 */
	public static void send(ServerPlayerEntity player, Identifier channel, PacketByteBuf buf) {
		getPlaySender(player).sendPacket(channel, buf);
	}

	@FunctionalInterface
	public interface LoginChannelHandler {
		/**
		 * Handles an incoming query response from a client.
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

	@FunctionalInterface
	public interface PlayChannelHandler {
		/**
		 * Handles an incoming packet.
		 *
		 * <p>This method is executed on {@linkplain io.netty.channel.EventLoop netty's event loops}.
		 * Modification to the game should be {@linkplain net.minecraft.util.thread.ThreadExecutor#submit(Runnable) scheduled} using the provided Minecraft server instance.
		 *
		 * <p>An example usage of this is to create an explosion where the player is looking:
		 * <blockquote><pre>
		 * ServerNetworking.getPlayReceiver().register(new Identifier("mymod", "boom"), (handler, server, sender, buf) -&rt; {
		 * 	boolean fire = buf.readBoolean();
		 *
		 * 	// All operations on the server or world must be executed on the server thread
		 * 	server.execute(() -&rt; {
		 * 		ModPacketHandler.createExplosion(handler.player, fire);
		 * 	});
		 * });
		 * </pre></blockquote>
		 *
		 * @param handler the network handler that received this packet
		 * @param server the server
		 * @param sender the packet sender
		 * @param buf the payload of the packet
		 */
		void receive(ServerPlayNetworkHandler handler, MinecraftServer server, PlayPacketSender sender, PacketByteBuf buf);
	}

	/**
	 * Allows blocking client log-in until all all futures passed into {@link LoginSynchronizer#waitFor(Future)} are done.
	 *
	 * @apiNote this interface is not intended to be implemented by users of api.
	 */
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
		 * <blockquote><pre>
		 * ServerNetworking.getLoginReceiver().register(CHECK_CHANNEL, (context, buf) -&gt; {
		 * 	if (!context.isUnderstood()) {
		 * 		handler.disconnect(new LiteralText("Only accept clients that can check!"));
		 * 		return;
		 * 	}
		 *
		 * 	String checkMessage = buf.readString(32767);
		 * 	ServerLoginNetworkHandler handler = context.getPacketListener();
		 * 	PacketSender sender = context.getPacketSender();
		 * 	MinecraftServer server = context.getEngine();
		 *
		 * 	// Just send the CompletableFuture returned by the server's submit method
		 * 	context.waitFor(server.submit(() -&gt; {
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
		 * </pre></blockquote>
		 * Usually it is enough to pass the return value for {@link net.minecraft.util.thread.ThreadExecutor#submit(Runnable)}
		 * for {@code future}.</p>
		 *
		 * @param future the future that must be done before the player can log in
		 */
		void waitFor(Future<?> future);
	}
}
