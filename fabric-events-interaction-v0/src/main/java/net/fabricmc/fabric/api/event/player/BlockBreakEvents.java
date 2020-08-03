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
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class BlockBreakEvents {
	private BlockBreakEvents() { }

	/**
	 * Callback before a block is broken.
	 * Only called on the server, however updates are synced with the client.
	 *
	 * <p>Upon return:
	 * <ul><li>SUCCESS/PASS passes on the callback to the next listener
	 * <li>CONSUME continues the default code for breaking the block and ignores all other listeners
	 * <li>FAIL cancels the block breaking action</ul>
	 */
	public static final Event<Before> BEFORE = EventFactory.createArrayBacked(Before.class,
			(listeners) -> (world, player, pos, state, entity) -> {
				for (Before event : listeners) {
					ActionResult result = event.beforeBlockBreak(world, player, pos, state, entity);

					if (result != ActionResult.PASS) {
						return result;
					}
				}

				return ActionResult.PASS;
			}
	);

	/*
	 * Callback after a block is broken.
	 * Called on the Server only
	 */
	public static final Event<After> AFTER = EventFactory.createArrayBacked(After.class,
			(listeners) -> (world, player, pos, state, entity) -> {
				for (After event : listeners) {
					event.afterBlockBreak(world, player, pos, state, entity);
				}
			}
	);

	/*
	 * Callback when a block break has been canceled
	 * Called on the Server only
	 */
	public static final Event<Cancel> CANCEL = EventFactory.createArrayBacked(Cancel.class,
			(listeners) -> (world, player, pos, state, entity) -> {
				for (Cancel event : listeners) {
					event.onBlockBreakCancel(world, player, pos, state, entity);
				}
			}
	);

	@FunctionalInterface
	public interface Before {
		/* Called before a block is broken
		 *
		 * <p>Fields:
		 * <ul><li> world - The world at which the block is being broken
		 * <li> player - The player who is breaking the block
		 * <li> pos - The position at which the block is being broken
		 * <li> state - The block state from BEFORE the block is broken
		 * <li> entity - The block entity from BEFORE the block is broken (can be null)
		 */
		ActionResult beforeBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, /* Nullable */ BlockEntity entity);
	}

	@FunctionalInterface
	public interface After {
		/* Called after a block is broken
		 *
		 * <p>Fields:
		 * <ul><li> world - The world where the block was broken
		 * <li> player - The player who broke the block
		 * <li> pos - The position where the block was broken
		 * <li> state - The block state from AFTER the block was broken
		 * <li> entity - The block entity of the broken block (can be null)
		 * <li> block - The block instance of the block that was broken</ul>
		 */
		void afterBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, /* Nullable */ BlockEntity entity);
	}

	@FunctionalInterface
	public interface Cancel {
		/* Called when a block break has been canceled
		 *
		 * <p>Fields:
		 * <ul><li> world - The world where the block was going to be broken
		 * <li> player - The player was going to break the block
		 * <li> pos - The position where the block was going to be broken
		 * <li> state - The block state of the block that was going to be broken
		 * <li> entity - The block entity of the block that was going to be broken (can be null)
		 * <li> block - The block instance of the block that was going to be broken</ul>
		 */
		void onBlockBreakCancel(World world, PlayerEntity player, BlockPos pos, BlockState state, /* Nullable */ BlockEntity entity);
	}
}
