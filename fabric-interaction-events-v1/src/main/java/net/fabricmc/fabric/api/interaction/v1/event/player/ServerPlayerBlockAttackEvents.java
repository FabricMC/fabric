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

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Events related to a player attacking a block.
 * An attack action is typically bound to the left-click mouse button.
 */
public final class ServerPlayerBlockAttackEvents {
	public static final Event<Allow> ALLOW = EventFactory.createArrayBacked(Allow.class, callbacks -> (player, world, hand, pos, direction) -> {
		for (Allow callback : callbacks) {
			if (!callback.allowBlockAttack(player, world, hand, pos, direction)) {
				return false;
			}
		}

		return true;
	});

	public static final Event<Before> BEFORE = EventFactory.createArrayBacked(Before.class, callbacks -> (player, world, hand, pos, direction) -> {
		for (Before callback : callbacks) {
			callback.beforeBlockAttack(player, world, hand, pos, direction);
		}
	});

	// FIXME: Not implemented yet!
	public static final Event<After> AFTER = EventFactory.createArrayBacked(After.class, callbacks -> (player, world, hand, pos, direction) -> {
		for (After callback : callbacks) {
			callback.afterBlockAttack(player, world, hand, pos, direction);
		}
	});

	public static final Event<Canceled> CANCELLED = EventFactory.createArrayBacked(Canceled.class, callbacks -> (player, world, hand, pos, direction) -> {
		for (Canceled callback : callbacks) {
			callback.onBlockAttackCanceled(player, world, hand, pos, direction);
		}
	});

	private ServerPlayerBlockAttackEvents() {
	}

	@FunctionalInterface
	public interface Allow {
		boolean allowBlockAttack(ServerPlayerEntity player, ServerWorld world, Hand hand, BlockPos pos, Direction direction);
	}

	@FunctionalInterface
	public interface Before {
		void beforeBlockAttack(ServerPlayerEntity player, ServerWorld world, Hand hand, BlockPos pos, Direction direction);
	}

	@FunctionalInterface
	public interface After {
		void afterBlockAttack(ServerPlayerEntity player, ServerWorld world, Hand hand, BlockPos pos, Direction direction);
	}

	@FunctionalInterface
	public interface Canceled {
		void onBlockAttackCanceled(ServerPlayerEntity player, ServerWorld world, Hand hand, BlockPos pos, Direction direction);
	}
}
