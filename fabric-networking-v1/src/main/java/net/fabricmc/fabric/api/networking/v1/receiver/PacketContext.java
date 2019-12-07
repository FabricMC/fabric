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

import net.minecraft.network.listener.PacketListener;
import net.minecraft.util.thread.ThreadExecutor;

/**
 * A context for packet receiving. Offers access to additional information, such as the
 * network handler, the executor for code to execute on the main thread of the game
 * engine, etc.
 *
 * <p>The implementation is safe to be passed to the task scheduled to the game engine,
 * unlike the packet buffer that must be released after the packet receiver has handled
 * the packet.
 *
 * <p>It has sub-interfaces that offers more versatile access for different packet
 * receiving situations.
 *
 * @see PacketReceiver
 */
public interface PacketContext {
	/**
	 * Gets the game engine, which is also a thread executor.
	 *
	 * @return the executor
	 */
	ThreadExecutor<?> getEngine();

	/**
	 * Gets the associated network handler, or packet listener, that
	 * received this packet.
	 *
	 * @return the network handler
	 */
	PacketListener getNetworkHandler();
}
