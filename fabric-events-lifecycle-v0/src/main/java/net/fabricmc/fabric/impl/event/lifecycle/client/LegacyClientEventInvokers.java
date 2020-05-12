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

package net.fabricmc.fabric.impl.event.lifecycle.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.api.event.client.ItemTooltipCallback;
import net.fabricmc.fabric.api.event.world.WorldTickCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class LegacyClientEventInvokers implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Allows deprecated events to still be invoked by the newer implementations
		ClientLifecycleEvents.CLIENT_TICK.register(ClientTickCallback.EVENT.invoker()::tick);
		ClientLifecycleEvents.WORLD_TICK.register(WorldTickCallback.EVENT.invoker()::tick);
		// This is part of generic events now.
		net.fabricmc.fabric.api.client.event.generic.v1.item.ItemTooltipCallback.EVENT.register(ItemTooltipCallback.EVENT.invoker()::getTooltip);
	}
}
