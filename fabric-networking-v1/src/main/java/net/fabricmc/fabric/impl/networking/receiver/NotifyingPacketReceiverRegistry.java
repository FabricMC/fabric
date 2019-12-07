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

package net.fabricmc.fabric.impl.networking.receiver;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.receiver.PacketContext;
import net.fabricmc.fabric.api.networking.v1.receiver.PacketReceiver;

abstract class NotifyingPacketReceiverRegistry<T extends PacketContext> extends ThreadSafePacketReceiverRegistry<T> {
	@Override
	public boolean register(Identifier channel, PacketReceiver<? super T> receiver) {
		if (!super.register(channel, receiver)) return false;

		notify(true, channel);
		return true;
	}

	@Override
	public boolean unregister(Identifier channel) {
		if (!super.unregister(channel)) return false;

		notify(false, channel);
		return true;
	}

	protected abstract void notify(boolean register, Identifier channel);
}
