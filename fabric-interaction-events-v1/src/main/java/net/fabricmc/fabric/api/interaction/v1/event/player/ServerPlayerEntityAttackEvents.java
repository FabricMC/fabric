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

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ServerPlayerEntityAttackEvents {
	public static final Event<Allow> ALLOW = EventFactory.createArrayBacked(Allow.class, callbacks -> (player, world, hand, target) -> {
		for (Allow callback : callbacks) {
			if (!callback.allowEntityAttack(player, world, hand, target)) {
				return false;
			}
		}

		return true;
	});

	public static final Event<Before> BEFORE = EventFactory.createArrayBacked(Before.class, callbacks -> (player, world, hand, target) -> {
		for (Before callback : callbacks) {
			callback.beforeEntityAttack(player, world, hand, target);
		}
	});

	public static final Event<After> AFTER = EventFactory.createArrayBacked(After.class, callbacks -> (player, world, hand, target) -> {
		for (After callback : callbacks) {
			callback.afterEntityAttack(player, world, hand, target);
		}
	});

	private ServerPlayerEntityAttackEvents() {
	}

	@FunctionalInterface
	public interface Allow {
		boolean allowEntityAttack(ServerPlayerEntity player, ServerWorld world, Hand hand, Entity target);
	}

	@FunctionalInterface
	public interface Before {
		void beforeEntityAttack(ServerPlayerEntity player, ServerWorld world, Hand hand, Entity target);
	}

	@FunctionalInterface
	public interface After {
		void afterEntityAttack(ServerPlayerEntity player, ServerWorld world, Hand hand, Entity target);
	}
}
