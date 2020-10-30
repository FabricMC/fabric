package net.fabricmc.fabric.api.client.networking.v1.login;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.PacketByteBuf;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ChannelHandlerRegistry;
import net.fabricmc.fabric.impl.networking.client.ClientNetworkingImpl;

@Environment(EnvType.CLIENT)
public final class ClientLoginNetworking {
	/**
	 * Returns the packet receiver for channel handler registration on client login network handlers, receiving {@link net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket login query request packets}.
	 */
	public static ChannelHandlerRegistry<LoginChannelHandler> getLoginReceivers() {
		return ClientNetworkingImpl.LOGIN;
	}

	private ClientLoginNetworking() {
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface LoginChannelHandler {
		/**
		 * Handles an incoming query request from a server.
		 *
		 * <p>This method is executed on {@linkplain io.netty.channel.EventLoop netty's event loops}.
		 * Modification to the game should be {@linkplain net.minecraft.util.thread.ThreadExecutor#submit(Runnable) scheduled} using the provided Minecraft client instance.
		 *
		 * <p>The return value of this method is a completable future that may be used to delay the login process to the server until a task {@link CompletableFuture#isDone() is done}.
		 *
		 * @param handler the network handler that received this packet
		 * @param client the client
		 * @param buf the payload of the packet
		 * @param listenerAdder listeners to be called when the response packet is sent to the server
		 * @return a completable future which contains the payload to respond to the server with.
		 * If the future contains {@code null}, then the server will be notified that the client did not understand the query.
		 */
		CompletableFuture<@Nullable PacketByteBuf> receive(ClientLoginNetworkHandler handler, MinecraftClient client, PacketByteBuf buf, Consumer<GenericFutureListener<? extends Future<? super Void>>> listenerAdder);
	}
}
