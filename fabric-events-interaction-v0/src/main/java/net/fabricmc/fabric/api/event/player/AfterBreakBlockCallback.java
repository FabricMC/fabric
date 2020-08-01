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
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Callback after a block is broken.
 * Only called on the server, however updates are synced with the client.
 */
public interface AfterBreakBlockCallback {
	Event<AfterBreakBlockCallback> EVENT = EventFactory.createArrayBacked(AfterBreakBlockCallback.class,
			(listeners) -> (pos, state, entity, block) -> {
				for (AfterBreakBlockCallback event : listeners) {
					event.afterBlockBreak(pos, state, entity, block);
				}
			}
	);

	void afterBlockBreak(BlockPos pos, BlockState state, BlockEntity entity, Block block);
}
