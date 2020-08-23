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

package net.fabricmc.fabric.api.event.player;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class PlayerBlockBreakEvents {
	private PlayerBlockBreakEvents() { }

	/**
	 * Callback before a block is broken.
	 * Only called on the server, however updates are synced with the client.
	 *
	 * <p>If any listener cancels a block breaking action, that block breaking
	 * action is cancelled and {@link CANCELED} event is fired. Otherwise, the
	 * {@link AFTER} event is fired.</p>
	 */
	public static final Event<Before> BEFORE = EventFactory.createArrayBacked(Before.class,
			(listeners) -> (world, player, pos, state, entity) -> {
				for (Before event : listeners) {
					boolean result = event.beforeBlockBreak(world, player, pos, state, entity);

					if (!result) {
						return false;
					}
				}

				return true;
			}
	);

	/**
	 * Callback after a block is broken.
	 *
	 * <p>Only called on a logical server.
	 */
	public static final Event<After> AFTER = EventFactory.createArrayBacked(After.class,
			(listeners) -> (world, player, pos, state, entity) -> {
				for (After event : listeners) {
					event.afterBlockBreak(world, player, pos, state, entity);
				}
			}
	);

	/**
	 * Callback when a block break has been canceled.
	 *
	 * <p>Only called on a logical server. May be used to send packets to revert client-side block changes.
	 */
	public static final Event<Canceled> CANCELED = EventFactory.createArrayBacked(Canceled.class,
			(listeners) -> (world, player, pos, state, entity) -> {
				for (Canceled event : listeners) {
					event.onBlockBreakCanceled(world, player, pos, state, entity);
				}
			}
	);

	@FunctionalInterface
	public interface Before {
		/**
		 * Called before a block is broken and allows cancelling the block breaking.
		 *
		 * <p>Implementations should not modify the world or assume the block break has completed or failed.</p>
		 *
		 * @param world the world in which the block is broken
		 * @param player the player breaking the block
		 * @param pos the position at which the block is broken
		 * @param state the block state <strong>before</strong> the block is broken
		 * @param blockEntity the block entity <strong>before</strong> the block is broken, can be {@code null}
		 * @return {@code false} to cancel block breaking action, or {@code true} to pass to next listener
		 */
		boolean beforeBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, /* Nullable */ BlockEntity blockEntity);
	}

	@FunctionalInterface
	public interface After {
		/**
		 * Called after a block is successfully broken.
		 *
		 * @param world the world where the block was broken
		 * @param player the player who broke the block
		 * @param pos the position where the block was broken
		 * @param state the block state <strong>before</strong> the block was broken
		 * @param blockEntity the block entity of the broken block, can be {@code null}
		 */
		void afterBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, /* Nullable */ BlockEntity blockEntity);
	}

	@FunctionalInterface
	public interface Canceled {
		/**
		 * Called when a block break has been canceled.
		 *
		 * @param world the world where the block was going to be broken
		 * @param player the player who was going to break the block
		 * @param pos the position where the block was going to be broken
		 * @param state the block state of the block that was going to be broken
		 * @param blockEntity the block entity of the block that was going to be broken, can be {@code null}
		 */
		void onBlockBreakCanceled(World world, PlayerEntity player, BlockPos pos, BlockState state, /* Nullable */ BlockEntity blockEntity);
	}
}
