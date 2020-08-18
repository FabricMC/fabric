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

import net.minecraft.entity.Entity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Callback for the effects displayed when a block is broken (particles and sounds)
 *
 * <p>Upon return:
 * <ul><li>SUCCESS cancels further processing.
 * <li>PASS falls back to further processing.
 * <li>FAIL cancels further processing and prevents the block break effects from being displayed.</ul>
 */
public interface BlockBreakEffectsCallback {
	Event<BlockBreakEffectsCallback> EVENT = EventFactory.createArrayBacked(BlockBreakEffectsCallback.class,
			(listeners) -> (world, breakingEntity, pos) -> {
				for (BlockBreakEffectsCallback event : listeners) {
					ActionResult result = event.run(world, breakingEntity, pos);

					if (result != ActionResult.PASS) {
						return result;
					}
				}

				return ActionResult.PASS;
			}
	);

	ActionResult run(World world, /* nullable */ Entity breakingEntity, BlockPos pos);
}
