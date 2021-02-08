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

package net.fabricmc.fabric.api.interaction.v1.event.player;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ServerPlayerEntityInteractEvents {
	public static final Event<Allow> ALLOW = EventFactory.createArrayBacked(Allow.class, callbacks -> (player, world, hand, targetEntity, hit) -> {
		for (Allow callback : callbacks) {
			if (!callback.allowEntityInteraction(player, world, hand, targetEntity, hit)) {
				return false;
			}
		}

		return true;
	});

	public static final Event<Before> BEFORE = EventFactory.createArrayBacked(Before.class, callbacks -> (player, world, hand, targetEntity, hit) -> {
		for (Before callback : callbacks) {
			callback.beforeEntityInteraction(player, world, hand, targetEntity, hit);
		}
	});

	public static final Event<After> AFTER = EventFactory.createArrayBacked(After.class, callbacks -> (player, world, hand, targetEntity, hit) -> {
		for (After callback : callbacks) {
			callback.afterEntityInteraction(player, world, hand, targetEntity, hit);
		}
	});
	
	private ServerPlayerEntityInteractEvents() {
	}

	@FunctionalInterface
	public interface Allow {
		boolean allowEntityInteraction(ServerPlayerEntity player, ServerWorld world, Hand hand, Entity targetEntity, EntityHitResult hit);
	}

	@FunctionalInterface
	public interface Before {
		void beforeEntityInteraction(ServerPlayerEntity player, ServerWorld world, Hand hand, Entity targetEntity, EntityHitResult hit);
	}

	@FunctionalInterface
	public interface After {
		void afterEntityInteraction(ServerPlayerEntity player, ServerWorld world, Hand hand, Entity targetEntity, EntityHitResult hit);
	}
}
