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

package net.fabricmc.fabric.api.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.Packet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// TODO: javadocs
public class EntityTrackingRegistry {
	private static final Logger LOGGER = LogManager.getLogger();
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

	public static final EntityTrackingRegistry INSTANCE = new EntityTrackingRegistry();
	private final Map<EntityType, Entry> entries = new HashMap<>();

	private EntityTrackingRegistry() {

	}

	public Entry get(EntityType type) {
		return entries.get(type);
	}

	@Deprecated
	public Packet createSpawnPacket(Entity entity) {
		return null;
	}

	public void register(EntityType type, int trackingDistance, int updateIntervalTicks) {
		register(type, trackingDistance, updateIntervalTicks, true);
	}

	public void register(EntityType type, int trackingDistance, int updateIntervalTicks, boolean alwaysUpdateVelocity) {
		entries.put(type, new Entry(trackingDistance, updateIntervalTicks, alwaysUpdateVelocity));
	}

	@Deprecated
	public void registerSpawnPacketProvider(EntityType type, Function<Entity, Packet> packetFunction) {
		LOGGER.warn("[EntityTrackingRegistry] As of 19w05a, registerSpawnPacketProvider is a no-op! Update your mod!");
	}
}
