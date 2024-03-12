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

package net.fabricmc.fabric.api.datafixer.v1;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

public final class DataFixerEvents {
	private static final String ENTRYPOINT_KEY = "fabric-data-fixer";
	/**
	 * Called when vanilla schema 1906 registers block entities.
	 */
	public static final Event<RegisterBlockEntities> REGISTER_BLOCK_ENTITIES = EventFactory.createArrayBacked(RegisterBlockEntities.class, callbacks -> (registry, schema) -> {
		List<EntrypointContainer<DataFixerEntrypoint>> dataFixerEntrypoints = FabricLoader.getInstance().getEntrypointContainers(ENTRYPOINT_KEY, DataFixerEntrypoint.class);
		List<DataFixerEntrypoint> entrypoints = dataFixerEntrypoints.stream().map(EntrypointContainer::getEntrypoint).toList();
		for (DataFixerEntrypoint entrypoint : entrypoints) {
			entrypoint.onRegisterBlockEntities(registry, schema);
		}
		for (RegisterBlockEntities callback : callbacks) {
			callback.onRegisterBlockEntities(registry, schema);
		}
	});

	/**
	 * Called when vanilla schema 1904 registers entities.
	 */
	public static final Event<RegisterEntities> REGISTER_ENTITIES = EventFactory.createArrayBacked(RegisterEntities.class, callbacks -> (registry, schema) -> {
		List<EntrypointContainer<DataFixerEntrypoint>> dataFixerEntrypoints = FabricLoader.getInstance().getEntrypointContainers(ENTRYPOINT_KEY, DataFixerEntrypoint.class);
		List<DataFixerEntrypoint> entrypoints = dataFixerEntrypoints.stream().map(EntrypointContainer::getEntrypoint).toList();
		for (DataFixerEntrypoint entrypoint : entrypoints) {
			entrypoint.onRegisterEntities(registry, schema);
		}
		for (RegisterEntities callback : callbacks) {
			callback.onRegisterEntities(registry, schema);
		}
	});

	private DataFixerEvents() {
	}

	@FunctionalInterface
	public interface RegisterBlockEntities {
		void onRegisterBlockEntities(Map<String, Supplier<TypeTemplate>> registry, Schema schema);
	}

	@FunctionalInterface
	public interface RegisterEntities {
		void onRegisterEntities(Map<String, Supplier<TypeTemplate>> registry, Schema schema);
	}
}
