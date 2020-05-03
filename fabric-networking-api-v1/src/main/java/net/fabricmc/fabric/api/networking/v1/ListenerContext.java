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

import net.minecraft.network.listener.PacketListener;
import net.minecraft.util.thread.ThreadExecutor;

/**
 * Represents a context for {@linkplain PacketReceiver packet reception} in a channel.
 *
 * @see PacketReceiver
 */
public interface ListenerContext {
	/**
	 * Returns the packet listener that received this packet.
	 *
	 * <p>The packet listener offers access to the {@linkplain PacketListener#getConnection()
	 * connection}.</p>
	 *
	 * @return the packet listener
	 */
	PacketListener getListener();

	/**
	 * Returns the game engine associated with the packet listener.
	 *
	 * <p>The game engine exposes access to synchronization of execution to main
	 * thread, such as {@link ThreadExecutor#submit(Runnable)}, allowing you to
	 * apply changes to the game without danger of concurrent modification.</p>
	 *
	 * @return the game engine
	 */
	ThreadExecutor<?> getEngine();
}
