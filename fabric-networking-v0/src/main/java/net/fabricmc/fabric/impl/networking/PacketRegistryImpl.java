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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketRegistry;
import net.fabricmc.fabric.api.networking.v1.receiver.PacketReceiverRegistry;
import net.fabricmc.fabric.api.networking.v1.receiver.PlayPacketContext;

public abstract class PacketRegistryImpl implements PacketRegistry {
	protected static final Logger LOGGER = LogManager.getLogger();

	PacketRegistryImpl() {
	}

	@Override
	public void register(Identifier id, PacketConsumer consumer) {
		getReceiverRegistry().register(id, (context, buffer) -> consumer.accept(new WrappedPacketContext(context), buffer));
	}

	@Override
	public void unregister(Identifier id) {
		getReceiverRegistry().unregister(id);
	}

	abstract PacketReceiverRegistry<? extends PlayPacketContext> getReceiverRegistry();
}
