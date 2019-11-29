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

package net.fabricmc.fabric.api.entity;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;

/**
 * Registry for server-&gt;client entity tracking values.
 *
 * @deprecated Use FabricEntityTypeBuilder methods
 */
@SuppressWarnings("deprecation")
@Deprecated
public class EntityTrackingRegistry {
	private static final Logger LOGGER = LogManager.getLogger();

	@Deprecated
	public static class Entry {
		private final int trackingDistance;
		private final int updateIntervalTicks;
		private final boolean alwaysUpdateVelocity;

		public Entry(int trackingDistance, int updateIntervalTicks, boolean alwaysUpdateVelocity) {
			this.trackingDistance = trackingDistance;
			this.updateIntervalTicks = updateIntervalTicks;
			this.alwaysUpdateVelocity = alwaysUpdateVelocity;
		}

		public int getTrackingDistance() {
			return trackingDistance;
		}

		public int getUpdateIntervalTicks() {
			return updateIntervalTicks;
		}

		public boolean alwaysUpdateVelocity() {
			return alwaysUpdateVelocity;
		}
	}

	@Deprecated
	public static final EntityTrackingRegistry INSTANCE = new EntityTrackingRegistry();
	private final Map<EntityType, Entry> entries = new HashMap<>();

	private EntityTrackingRegistry() { }

	@Deprecated
	public Entry get(EntityType type) {
		return entries.get(type);
	}

	@Deprecated
	public void register(EntityType type, int trackingDistance, int updateIntervalTicks) {
		register(type, trackingDistance, updateIntervalTicks, true);
	}

	@Deprecated
	public void register(EntityType type, int trackingDistance, int updateIntervalTicks, boolean alwaysUpdateVelocity) {
		LOGGER.warn("Deprecation warning: As of February 2019, registering tracking via EntityTrackingRegistry is no longer effective. Use FabricEntityTypeBuilder. (Entity: " + Registry.ENTITY_TYPE.getId(type) + ")");
		entries.put(type, new Entry(trackingDistance, updateIntervalTicks, alwaysUpdateVelocity));
	}
}
