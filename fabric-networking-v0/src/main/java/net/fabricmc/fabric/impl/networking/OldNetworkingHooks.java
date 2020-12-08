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

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.network.C2SPacketTypeCallback;
import net.fabricmc.fabric.api.networking.v1.S2CPlayChannelEvents;

public final class OldNetworkingHooks implements ModInitializer {
	@Override
	public void onInitialize() {
		// Must be lambdas below
		S2CPlayChannelEvents.REGISTER.register((handler, server, sender, channels) -> {
			C2SPacketTypeCallback.REGISTERED.invoker().accept(handler.player, channels);
		});
		S2CPlayChannelEvents.UNREGISTER.register((handler, server, sender, channels) -> {
			C2SPacketTypeCallback.UNREGISTERED.invoker().accept(handler.player, channels);
		});
	}
}
