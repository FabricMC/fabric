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

package net.fabricmc.fabric.api.event;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Callback for the effects displayed when a block is broken (particles and sounds).
 *
 * <p>This is invoked on both the logical-client and logical-server
 */
public interface BlockBreakEffectsCallback {
	Event<BlockBreakEffectsCallback> EVENT = EventFactory.createArrayBacked(BlockBreakEffectsCallback.class, (listeners) -> (world, pos, state, breakingEntity) -> {
		for (BlockBreakEffectsCallback event : listeners) {
			if (!event.run(world, pos, state, breakingEntity)) {
				return false;
			}
		}

		return true;
	});

	/**
	 * @param world World
	 * @param pos Position Of Broken Block
	 * @param state Block State of Broken Block
	 * @param breakingEntity Entity That Broke The Block
	 * @return <code>true</code> falls back to further processing, <code>false</code> cancels further processing and prevents the block break effects from being displayed.
	 */
	boolean run(World world, BlockPos pos, BlockState state, /* nullable */ Entity breakingEntity);
}
