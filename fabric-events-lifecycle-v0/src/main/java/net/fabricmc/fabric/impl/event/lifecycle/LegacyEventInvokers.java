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

package net.fabricmc.fabric.impl.event.lifecycle;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.api.event.server.ServerStopCallback;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.fabricmc.fabric.api.event.world.WorldTickCallback;

public class LegacyEventInvokers implements ModInitializer {
	@Override
	public void onInitialize() {
		// Allows deprecated events to still be invoked by the newer implementations
		ServerLifecycleEvents.SERVER_START.register(ServerStartCallback.EVENT.invoker()::onStartServer);
		ServerLifecycleEvents.SERVER_STOP.register(ServerStopCallback.EVENT.invoker()::onStopServer);
		ServerLifecycleEvents.SERVER_TICK.register(ServerTickCallback.EVENT.invoker()::tick);
		ServerLifecycleEvents.WORLD_TICK.register(WorldTickCallback.EVENT.invoker()::tick);
	}
}
