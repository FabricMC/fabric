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

package net.fabricmc.fabric.api.networking.v1.server;

import java.util.concurrent.Future;

import net.minecraft.server.network.ServerLoginNetworkHandler;

import net.fabricmc.fabric.api.networking.v1.ChannelHandler;
import net.fabricmc.fabric.api.networking.v1.ListenerContext;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.util.PacketByteBufs;

/**
 * Represents the context for {@link ServerNetworking#getLoginReceiver()}, in which a
 * {@link net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket login query
 * response packet} is received.
 *
 * <p>Since the response is always received after the query is sent even when the client
 * doesn't handle the channel of the query, {@link #isUnderstood()} <strong>must</strong>
 * be checked when handling in this context.</p>
 *
 * @see ServerNetworking#getLoginReceiver()
 */
public interface ServerLoginContext extends ServerContext {
	/**
	 * {@inheritDoc}
	 *
	 * <p>In a server login context, the network handler is always a {@link
	 * ServerLoginNetworkHandler}.</p>
	 */
	@Override
	ServerLoginNetworkHandler getListener();

	/**
	 * Returns a packet sender that can send additional query request packets.
	 *
	 * <p>If an upcoming query request packet cannot be immediately sent after
	 * receiving this packet, call {@link #waitFor(Future)}, which will make
	 * sure the client is not admitted until the future is {@linkplain Future#isDone()
	 * done}.</p>
	 *
	 * @return the packet sender for query requests
	 */
	PacketSender getPacketSender();

	// packet info

	/* (Non-Javadoc)
	 * Returns the integer ID of the query response.
	 */
	//int getQueryId(); Expose again when needed

	/**
	 * Returns whether the original query request with the same query ID as this response was understood.
	 *
	 * <p>If the query response is not understood, an {@link PacketByteBufs#empty()
	 * empty packet byte buf} will be passed as the {@code buf} for
	 * {@link ChannelHandler#receive(ListenerContext, net.minecraft.network.PacketByteBuf)}.</p>
	 *
	 * <p>Since it is never guaranteed that a client can always understand
	 * query requests, this method should <strong>always</strong> be checked in packet
	 * reception.</p>
	 *
	 * @return whether the query request was understood
	 */
	boolean isUnderstood();

	// utility

	/**
	 * Allows blocking client log-in until the {@code future} is {@link Future#isDone() done}.
	 *
	 * <p>Since packet reception happens on netty's event loops, this allows handlers to
	 * perform logic on the Server Thread, etc. For instance, a handler can prepare an
	 * {@linkplain #getPacketSender() upcoming query request} or check necessary login
	 * data on the server thread.</p>
	 *
	 * <p>Here is an example where the player log-in is blocked so that a credential check and
	 * building of a followup query request can be performed properly on the logical server
	 * thread before the player successfully logs in:
	 * <pre><blockquote>
	 *     ServerNetworking.getLoginReceiver().register(CHECK_CHANNEL, (context, buf) -&gt; {
	 *         if (!context.isUnderstood()) {
	 *             handler.disconnect(new LiteralText("Only accept clients that can check!"));
	 *             return;
	 *         }
	 *         String checkMessage = buf.readString(32767);
	 *         ServerLoginNetworkHandler handler = context.getPacketListener();
	 *         PacketSender sender = context.getPacketSender();
	 *         MinecraftServer server = context.getEngine();
	 *         // Just send the CompletableFuture returned by the server's submit method
	 *         context.waitFor(server.submit(() -&gt; {
	 *             LoginInfoChecker checker = LoginInfoChecker.get(server);
	 *             if (!checker.check(handler.getConnectionInfo(), checkMessage)) {
	 *                 handler.disconnect(new LiteralText("Invalid credentials!"));
	 *                 return;
	 *             }
	 *             sender.send(UPCOMING_CHECK, checker.buildSecondQueryPacket(handler, checkMessage));
	 *         }));
	 *     });
	 * </blockquote></pre>
	 * Usually it is enough to pass the return value for {@link net.minecraft.util.thread.ThreadExecutor#submit(Runnable)}
	 * for {@code future}.</p>
	 *
	 * @param future the future that must be done before the player can log in
	 */
	void waitFor(Future<?> future);
}
