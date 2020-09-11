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

package net.fabricmc.fabric.api.event.client.player;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ClientBlockBreakEvent {
	private ClientBlockBreakEvent() { }

	/**
	 * Callback before a block is to begin breaking.
	 *
	 * <p>If any listener cancels a block breaking action, that block breaking
	 * action is canceled and {@link #ON_CANCEL} event is fired. Otherwise the
	 * {@link #ON_PROGRESS} event is fired with a progress of 0.
	 */
	public static final Event<OnStart> ON_START = EventFactory.createArrayBacked(
			OnStart.class,
			listeners -> (world, player, pos, state, progress) -> {
				for (OnStart event : listeners) {
					boolean result = event.onBlockBreakStart(world, player, pos, state, progress);

					if (!result) {
						return false;
					}
				}

				return true;
			}
	);

	/**
	 * Callback during a blocks breaking stages
	 *
	 * <p>If any listener cancels a block progress action, that block
	 * action is canceled and {@link #ON_CANCEL} event is fired.
	 */
	public static final Event<OnProgress> ON_PROGRESS = EventFactory.createArrayBacked(
			OnProgress.class,
			listeners -> (world, player, pos, state, progress) -> {
				for (OnProgress event : listeners) {
					boolean result = event.onBlockBreakProgress(world, player, pos, state, progress);

					if (!result) {
						return false;
					}
				}

				return true;
			}
	);

	/**
	 * Callback after a block is broken.
	 * At the point of invocation you can ensure the client block has been destroyed from the world
	 */
	public static final Event<OnBreak> ON_BREAK = EventFactory.createArrayBacked(
			OnBreak.class,
			listeners -> (world, player, pos, state, progress) -> {
				for (OnBreak event : listeners) {
					event.onBlockBreak(world, player, pos, state, progress);
				}
			}
	);

	/**
	 * Callback when a block break has been canceled.
	 *
	 * <p>Can be used to stop a client from breaking a block if maybe a mod only wants to
	 * let you break a certain block at a certain time.
	 */
	public static final Event<OnCancel> ON_CANCEL = EventFactory.createArrayBacked(
			OnCancel.class,
			listeners -> (world, player, pos, state, progress) -> {
				for (OnCancel event : listeners) {
					event.onBlockBreakCancel(world, player, pos, state, progress);
				}
			}
	);

	@FunctionalInterface
	public interface OnStart {
		/**
		 * Called before a block is to begin breaking.
		 *
		 * <p>The block has not been removed or destroyed yet and is simply the player
		 * initiating the breaking of a block</p>
		 *
		 * @param world    the world in which the block is to be broken
		 * @param player   the player breaking the block
		 * @param pos      the position at which the block is to be broken
		 * @param state    the block state <strong>before</strong> the block is placed
		 * @param progress the progress of the break between 0.0-1.0 (Will always be 0 for this event)
		 *
		 * @return {@code false} to cancel block placing action, or {@code true} to pass to next listener
		 */
		boolean onBlockBreakStart(World world, PlayerEntity player, BlockPos pos, BlockState state, float progress);
	}

	@FunctionalInterface
	public interface OnProgress {
		/**
		 * Called during a blocks breaking stages.
		 *
		 * <p>The block has not been removed or destroyed yet and is simply the player
		 * continuing the breaking of a block</p>
		 *
		 * @param world    the world in which the block is placed
		 * @param player   the player placing the block
		 * @param pos      the position at which the block is placed
		 * @param state    the block state <strong>before</strong> the block is placed
		 * @param progress the progress of the break between 0.0-1.0
		 *
		 * @return {@code false} to cancel block placing action, or {@code true} to pass to next listener
		 */
		boolean onBlockBreakProgress(World world, PlayerEntity player, BlockPos pos, BlockState state, float progress);
	}

	@FunctionalInterface
	public interface OnBreak {
		/**
		 * Called after a block has been broken.
		 *
		 * @param world    the world where the block was broken
		 * @param player   the player who broke the block
		 * @param pos      the position where the block was broken
		 * @param state    the block state <strong>before</strong> the block was broken
		 * @param progress the progress of the break between 0.0-1.0 (Will always be 1 for this event)
		 */
		void onBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, float progress);
	}

	@FunctionalInterface
	public interface OnCancel {
		/**
		 * Called when a block break has been canceled.
		 * <p>This can be from both the {@link #ON_START} and {@link #ON_PROGRESS} events.</p>
		 *
		 * @param world    the world where the block was going to be broken
		 * @param player   the player who was going to break the block
		 * @param pos      the position where the block was going to be broken
		 * @param state    the block state of the block that was going to be broken
		 * @param progress the progress of the break before it was canceled between 0.0-1.0
		 */
		void onBlockBreakCancel(World world, PlayerEntity player, BlockPos pos, BlockState state, float progress);
	}
}
