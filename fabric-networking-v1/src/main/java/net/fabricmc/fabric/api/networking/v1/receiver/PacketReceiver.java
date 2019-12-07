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

package net.fabricmc.fabric.api.networking.v1.receiver;

import io.netty.buffer.ByteBuf;

import net.minecraft.util.PacketByteBuf;

import net.fabricmc.fabric.api.networking.v1.sender.PacketByteBufs;

/**
 * Receives custom-payload based packets from a certain channel.
 *
 * <p>The receiver is always run on the <b>network</b> thread, which is asynchronous from
 * the game thread. As a result, to read data from or write data to the game engine,
 * you should execute a task on the {@link PacketContext#getEngine() thread executor}
 * from the packet context passed.
 *
 * <p>It is recommended to do the following if you accesses/modifies the game:
 * <ol><li>Parse the buffer into an object or local variables in the
 * {@link #accept(PacketContext, PacketByteBuf)} method;
 * <li>Call {@link PacketContext#getEngine()} and {@link java.util.concurrent.Executor#execute(Runnable)}
 * with a runnable that applies changes to the game engine.</ol>
 *
 * @see PacketReceiverRegistry
 * @see PacketContext
 */
@FunctionalInterface
public interface PacketReceiver<T extends PacketContext> {
	/**
	 * Receive a custom payload based packet.
	 *
	 * <p>This method is supposed to be called on the <b>network</b> thread! As a
	 * result, to apply changes to the game engine, you should schedule the
	 * application runnable to the {@link PacketContext#getEngine() thread executor}
	 * after you have read data from the {@code buffer} to local variables.
	 *
	 * <p>The {@link PacketContext context} offers a few utilities. It is safe to be
	 * referenced or used in the task scheduled to the thread executor.
	 *
	 * <p>The {@code buffer} received will be released as soon as the method exits,
	 * meaning that you have to call {@link PacketByteBufs#retainedSlice(ByteBuf)}
	 * before sending it to the runnable and {@link ByteBuf#release()} when you finished
	 * using it in the runnable sent to the thread executor.
	 *
	 * <p>The {@code buffer} will be empty if a login query response indicates it
	 * failed to {@link ServerLoginQueryResponsePacketContext#isUnderstood() understand}
	 * a query.
	 *
	 * @param context the context as described above
	 * @param buffer the byte buffer containing the received packet data
	 * @see PacketContext
	 */
	void accept(T context, PacketByteBuf buffer);
}
