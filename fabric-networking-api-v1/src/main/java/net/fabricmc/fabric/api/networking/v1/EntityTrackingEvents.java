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

package net.fabricmc.fabric.api.networking.v1;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Events related to a tracking entities within a player's view distance.
 */
public final class EntityTrackingEvents {
	/**
	 * An event that is called before player starts tracking an entity.
	 * Typically this occurs when an entity enters a client's view distance.
	 * This event is called before the player's client is sent the entity's {@link Entity#createSpawnPacket() spawn packet}.
	 */
	public static final Event<StartTracking> START_TRACKING = EventFactory.createArrayBacked(StartTracking.class, callbacks -> (trackedEntity, player) -> {
		for (StartTracking callback : callbacks) {
			callback.onStartTracking(trackedEntity, player);
		}
	});

	/**
	 * An event that is called after a player has stopped tracking an entity.
	 * The client at this point was sent a packet to {@link net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket destroy} the entity on the client.
	 * The entity still exists on the server.
	 */
	public static final Event<StopTracking> STOP_TRACKING = EventFactory.createArrayBacked(StopTracking.class, callbacks -> (trackedEntity, player) -> {
		for (StopTracking callback : callbacks) {
			callback.onStopTracking(trackedEntity, player);
		}
	});

	@FunctionalInterface
	public interface StartTracking {
		/**
		 * Called before an entity starts getting tracked by a player.
		 *
		 * @param trackedEntity the entity that will be tracked
		 * @param player the player that will track the entity
		 */
		void onStartTracking(Entity trackedEntity, ServerPlayerEntity player);
	}

	@FunctionalInterface
	public interface StopTracking {
		/**
		 * Called after an entity stops getting tracked by a player.
		 *
		 * @param trackedEntity the entity that is no longer being tracked
		 * @param player the player that is no longer tracking the entity
		 */
		void onStopTracking(Entity trackedEntity, ServerPlayerEntity player);
	}

	private EntityTrackingEvents() {
	}
}
