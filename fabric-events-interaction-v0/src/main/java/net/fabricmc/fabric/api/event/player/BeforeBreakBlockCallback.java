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

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

/**
 * Callback before a block is broken.
 * Only called on the server, however updates are synced with the client.
 *
 * <p>Upon return:
 * <ul><li>SUCCESS/PASS/CONSUME continues the default code for breaking the block
 * <li>FAIL cancels the block breaking action
 */
public interface BeforeBreakBlockCallback {
	Event<BeforeBreakBlockCallback> EVENT = EventFactory.createArrayBacked(BeforeBreakBlockCallback.class,
			(listeners) -> (pos, state, entity, block) -> {
				for (BeforeBreakBlockCallback event : listeners) {
					ActionResult result = event.beforeBlockBreak(pos, state, entity, block);

					if (result != ActionResult.PASS) {
						return result;
					}
				}

				return ActionResult.PASS;
			}
	);

	ActionResult beforeBlockBreak(BlockPos pos, BlockState state, BlockEntity entity, Block block);
}
