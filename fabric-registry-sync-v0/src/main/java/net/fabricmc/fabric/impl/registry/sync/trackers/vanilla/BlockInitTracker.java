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

package net.fabricmc.fabric.impl.registry.sync.trackers.vanilla;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.MathHelper;

import net.fabricmc.fabric.mixin.registry.sync.DebugChunkGeneratorAccessor;

public final class BlockInitTracker {
	public static void postFreeze() {
		final List<BlockState> blockStateList = Registries.BLOCK.stream()
				.flatMap((block) -> block.getStateManager().getStates().stream())
				.toList();

		final int xLength = MathHelper.ceil(MathHelper.sqrt(blockStateList.size()));
		final int zLength = MathHelper.ceil(blockStateList.size() / (float) xLength);

		DebugChunkGeneratorAccessor.setBLOCK_STATES(blockStateList);
		DebugChunkGeneratorAccessor.setX_SIDE_LENGTH(xLength);
		DebugChunkGeneratorAccessor.setZ_SIDE_LENGTH(zLength);
	}
}
