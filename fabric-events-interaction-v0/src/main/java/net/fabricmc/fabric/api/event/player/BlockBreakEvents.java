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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class BlockBreakEvents {
	/**
	 * Callback before a block is broken.
	 * Only called on the server, however updates are synced with the client.
	 *
	 * <p>Upon return:
	 * <ul><li>SUCCESS/PASS/CONSUME continues the default code for breaking the block
	 * <li>FAIL cancels the block breaking action</ul>
	 *
	 * <p>Fields:
	 * <ul><li> world - The world at which the block is being broken
	 * <li> player - The player who is breaking the block
	 * <li> pos - The position at which the block is being broken
	 * <li> state - The block state from BEFORE the block is broken
	 * <li> entity - The block entity from BEFORE the block is broken (can be null)
	 * <li> block - The block instance of the block that is being broken</ul>
	 */
	public static final Event<BeforeBreakBlockCallback> BEFORE = EventFactory.createArrayBacked(BeforeBreakBlockCallback.class,
			(listeners) -> (world, player, pos, state, entity, block) -> {
				for (BeforeBreakBlockCallback event : listeners) {
					ActionResult result = event.beforeBlockBreak(world, player, pos, state, entity, block);

					if (result != ActionResult.FAIL) {
						return result;
					}
				}

				return ActionResult.FAIL;
			}
	);

	/**
	 * Callback after a block is broken.
	 * Called on both Client and Server
	 *
	 * <p>Fields:
	 * <ul><li> world - The world where the block was broken
	 * <li> player - The player who broke the block
	 * <li> pos - The position where the block was broken
	 * <li> state - The block state from AFTER the block was broken
	 * <li> entity - The block entity of the broken block (can be null)
	 * <li> block - The block instance of the block that was broken</ul>
	 */
	public static final Event<AfterBreakBlockCallback> AFTER = EventFactory.createArrayBacked(AfterBreakBlockCallback.class,
			(listeners) -> (world, player, pos, state, entity, block) -> {
				for (AfterBreakBlockCallback event : listeners) {
					event.afterBlockBreak(world, player, pos, state, entity, block);
				}
			}
	);

	@FunctionalInterface
	public interface BeforeBreakBlockCallback {
		ActionResult beforeBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, /* Nullable */ BlockEntity entity, Block block);
	}

	@FunctionalInterface
	public interface AfterBreakBlockCallback {
		void afterBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, /* Nullable */ BlockEntity entity, Block block);
	}
}
