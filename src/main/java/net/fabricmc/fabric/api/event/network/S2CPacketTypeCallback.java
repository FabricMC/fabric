/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.api.event.network;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Collection;

public interface S2CPacketTypeCallback {
	static final Event<S2CPacketTypeCallback> REGISTERED = EventFactory.createArrayBacked(
		S2CPacketTypeCallback.class,
		(callbacks) -> (types) -> {
			for (S2CPacketTypeCallback callback : callbacks) {
				callback.accept(types);
			}
		}
	);

	static final Event<S2CPacketTypeCallback> UNREGISTERED = EventFactory.createArrayBacked(
		S2CPacketTypeCallback.class,
		(callbacks) -> (types) -> {
			for (S2CPacketTypeCallback callback : callbacks) {
				callback.accept(types);
			}
		}
	);

	void accept(Collection<Identifier> types);
}
