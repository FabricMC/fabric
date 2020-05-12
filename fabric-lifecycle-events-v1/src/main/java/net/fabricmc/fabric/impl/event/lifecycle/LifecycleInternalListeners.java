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

import java.util.List;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class LifecycleInternalListeners implements ModInitializer {
	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			// We use the server shutdown to unload all entities and block entities so their events are fired.
			for (ServerWorld world : server.getWorlds()) {
				final List<Entity> entities = world.getEntities(null, entity -> true); // Get every single entity in the world

				for (Entity entity : entities) {
					ServerLifecycleEvents.ENTITY_UNLOAD.invoker().onEntityUnload(entity, world);
				}

				for (BlockEntity blockEntity : world.blockEntities) {
					ServerLifecycleEvents.BLOCK_ENTITY_UNLOAD.invoker().onUnloadBlockEntity(blockEntity, world);
				}
			}
		});
	}
}
