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

import net.fabricmc.fabric.api.client.interaction.v1.event.player.ClientPlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

/**
 * Events related to a player placing a block on a logical server.
 * Below is a diagram showing how the events are called, depending on whether {@link ServerPlayerBlockPlaceEvents#ALLOW} returns {@code true} or {@code false}.
 *
 * <pre>{@code
 *         (false) --> CANCELLED
 * ALLOW --|
 *         (true) --> BEFORE --> AFTER
 * }</pre>
 *
 * @see ServerPlayerBlockBreakEvents
 * @see ClientPlayerBlockBreakEvents
 */
public final class ServerPlayerBlockPlaceEvents {
	/**
	 * Callback before a block is placed.
	 * Only called on the server, however updates are synced with the client.
	 *
	 * <p>If any listener cancels a block placing action, that block placing action is cancelled and {@link ServerPlayerBlockPlaceEvents#CANCELED} event is called.
	 * Otherwise {@link ServerPlayerBlockPlaceEvents#BEFORE} event is called.
	 */
	public static final Event<Allow> ALLOW = EventFactory.createArrayBacked(Allow.class, callbacks -> (world, player, pos, futureState) -> {
		for (Allow callback : callbacks) {
			if (!callback.allowBlockPlace(world, player, pos, futureState)) {
				return false;
			}
		}

		return true;
	});

	/**
	 * Indication before a block has been placed.
	 * This event is not cancellable.
	 * To cancel a block being placed, use {@link ServerPlayerBlockPlaceEvents#ALLOW}.
	 */
	public static final Event<Before> BEFORE = EventFactory.createArrayBacked(Before.class, callbacks -> (world, player, pos, futureState) -> {
		for (Before callback : callbacks) {
			callback.beforeBlockPlace(world, player, pos, futureState);
		}
	});

	/**
	 * Indication that a block has successfully been placed.
	 */
	public static final Event<After> AFTER = EventFactory.createArrayBacked(After.class, callbacks -> (world, player, pos, futureState) -> {
		for (After callback : callbacks) {
			callback.afterBlockPlace(world, player, pos, futureState);
		}
	});

	/**
	 * Indication when a block placement has been canceled.
	 *
	 * <p>May be used to send packets to revert client-side block changes.
	 */
	public static final Event<Cancel> CANCELED = EventFactory.createArrayBacked(Cancel.class, callbacks -> (world, player, pos, futureState) -> {
		for (Cancel callback : callbacks) {
			callback.onBlockPlaceCanceled(world, player, pos, futureState);
		}
	});

	private ServerPlayerBlockPlaceEvents() {
	}

	@FunctionalInterface
	public interface Allow {
		/**
		 * Checks if a block should be allowed to be placed.
		 *
		 * <p>If any listener cancels a block placing action, that block placing action is cancelled and {@link ServerPlayerBlockPlaceEvents#CANCELED} event is called.
		 * Otherwise {@link ServerPlayerBlockPlaceEvents#BEFORE} event is called.
		 *
		 * @param world the world in which the block is placed
		 * @param player the player placing the block
		 * @param pos the position at which the block is placed
		 * @param futureState the block state that the set when the placing action is complete
		 * @return {@code false} to cancel the block placing action, or {@code true} to pass to {@link ServerPlayerBlockPlaceEvents#BEFORE}
		 */
		boolean allowBlockPlace(ServerWorld world, ServerPlayerEntity player, BlockPos pos, BlockState futureState);
	}

	@FunctionalInterface
	public interface Before {
		/**
		 * Called before a block is placed.
		 *
		 * @param world the world in which the block is placed
		 * @param player the player placing the block
		 * @param pos the position at which the block is placed
		 * @param futureState the block state that the set when the placing action is complete
		 */
		void beforeBlockPlace(ServerWorld world, ServerPlayerEntity player, BlockPos pos, BlockState futureState);
	}

	@FunctionalInterface
	public interface After {
		/**
		 * Called after a block is successfully placed.
		 *
		 * @param world the world in which the block is placed
		 * @param player the player placing the block
		 * @param pos the position at which the block is placed
		 * @param state the block state that the set by the placing action
		 */
		void afterBlockPlace(ServerWorld world, ServerPlayerEntity player, BlockPos pos, BlockState state);
	}

	@FunctionalInterface
	public interface Cancel {
		/**
		 * Called when placing a block has been cancelled.
		 *
		 * @param world the world in which the block is placed
		 * @param player the player placing the block
		 * @param pos the position at which the block is placed
		 * @param possibleState the block state that could have possibly been set if the placement was allowed to continue
		 */
		void onBlockPlaceCanceled(ServerWorld world, ServerPlayerEntity player, BlockPos pos, BlockState possibleState);
	}
}
