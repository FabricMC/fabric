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

package net.fabricmc.fabric.impl.networking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.ChannelHandler;
import net.fabricmc.fabric.api.networking.v1.ListenerContext;
import net.fabricmc.fabric.api.networking.v1.util.PacketByteBufs;

// client login
public abstract class ReceivingNetworkAddon<C extends ListenerContext> {
	protected final BasicPacketReceiver<C> receiver;

	protected ReceivingNetworkAddon(BasicPacketReceiver<C> receiver) {
		this.receiver = receiver;
	}

	// always supposed to handle async!
	protected boolean handle(Identifier channel, PacketByteBuf originalBuf, C context) {
		ChannelHandler<? super C> handler = this.receiver.get(channel);

		if (handler == null) {
			return false;
		}

		PacketByteBuf buf = PacketByteBufs.slice(originalBuf);

		try {
			handler.receive(context, buf);
		} catch (Throwable ex) {
			NetworkingDetails.LOGGER.error("Encountered exception while handling in channel \"{}\"", channel, ex);
			handler.rethrow(ex);
		}

		return true;
	}
}
