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

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCounted;

import net.minecraft.network.PacketByteBuf;

import net.fabricmc.fabric.api.networking.v1.client.ClientContext;

/**
 * Handles packets in a channel.
 *
 * <p>This is supposed to be implemented by API users to accomplish packet handling
 * functionalities.</p>
 *
 * @param <C> the listener context
 * @see PacketReceiver
 */
@FunctionalInterface
public interface ChannelHandler<C extends ListenerContext> {
	/**
	 * Receives a packet.
	 *
	 * <p>This method is executed on {@linkplain io.netty.channel.EventLoop netty's event loops}.
	 * Modification to the {@linkplain ListenerContext#getEngine() game} should be
	 * {@linkplain net.minecraft.util.thread.ThreadExecutor#submit(Runnable) scheduled}.</p>
	 *
	 * <p>The {@code buf} will be {@linkplain ReferenceCounted#release() released} on exiting this
	 * method. To ensure access to the buf later, you should {@link ReferenceCounted#retain()
	 * retain} the {@code buf}.</p>
	 *
	 * <p>An example usage can be like below, assuming {@code C} is a
	 * {@linkplain ClientContext client context}:
	 * <pre><blockquote>
	 *     (context, buf) -&rt; {
	 *         String message = buf.readString(32767);
	 *         context.getEngine().submit(() -&rt; {
	 *             context.getEngine().send(() -&rt; context.getEngine().inGameHud.setOverlayMessage(message, true));
	 *         });
	 *     }
	 * </blockquote></pre></p>
	 *
	 * <p>When this method throws an exception, it will be captured and logged. The exception
	 * will be fed to {@link #rethrow(Throwable)}, which by default throws the exception to
	 * the {@linkplain io.netty.channel.EventLoop event loop} and handled by {@link
	 * net.minecraft.network.ClientConnection#exceptionCaught(ChannelHandlerContext, Throwable)},
	 * causing a disconnection.</p>
	 *
	 * @param context the context for the packet
	 * @param buf     the content of the packet
	 */
	void receive(C context, PacketByteBuf buf);

	// todo do we need this rethrow functionality
	// throw networking errors, may report custom message as well
	// may throw OffThreadException.INSTANCE for example

	/**
	 * Handles a exception thrown by {@link #receive(ListenerContext, PacketByteBuf)}.
	 *
	 * <p>By default, this implementation will simply throw the captured exception.</p>
	 *
	 * <p>Throwables thrown by this method will be handled by {@link
	 * net.minecraft.network.ClientConnection#exceptionCaught(ChannelHandlerContext, Throwable)}.</p>
	 *
	 * @param ex  the captured exception
	 * @param <E> the throwable type variable, which allows throwing any throwable as unchecked
	 * @throws E any exception as a result of exception handling.
	 */
	@SuppressWarnings("unchecked")
	default <E extends Throwable> void rethrow(Throwable ex) throws E {
		throw (E) ex;
	}
}
