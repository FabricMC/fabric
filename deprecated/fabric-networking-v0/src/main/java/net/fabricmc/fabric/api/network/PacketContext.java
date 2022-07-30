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

package net.fabricmc.fabric.api.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.thread.ThreadExecutor;

import net.fabricmc.api.EnvType;

/**
 * Interface defining a context used during packet processing. Allows access
 * to additional information, such as the source/target of the player, or
 * the correct task queue to enqueue synchronization-requiring code on.
 */
@Deprecated
public interface PacketContext {
	/**
	 * Get the environment associated with the packet.
	 *
	 * @return EnvType.CLIENT if processing packet on the client side,
	 * EnvType.SERVER otherwise.
	 */
	EnvType getPacketEnvironment();

	/**
	 * Get the player associated with the packet.
	 *
	 * <p>On the client side, this always returns the client-side player instance.
	 * On the server side, it returns the player belonging to the client this
	 * packet was sent by.
	 *
	 * @return The player associated with the packet.
	 */
	PlayerEntity getPlayer();

	/**
	 * Get the task queue for a given side.
	 *
	 * <p>As Minecraft networking I/O is asynchronous, but a lot of its logic is
	 * not thread-safe, it is recommended to do the following:
	 *
	 * <ul><li>read and parse the PacketByteBuf,
	 * <li>run the packet response logic through the main thread task queue via
	 * ThreadTaskQueue.execute(). The method will check if it's not already
	 * on the main thread in order to avoid unnecessary delays, so don't
	 * worry about that!</ul>
	 *
	 * @return The thread task queue.
	 */
	ThreadExecutor getTaskQueue();
}
