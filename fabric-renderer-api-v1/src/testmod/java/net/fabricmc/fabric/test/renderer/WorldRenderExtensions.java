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

package net.fabricmc.fabric.test.renderer;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Extension interface for a world to notify the world that a block needs to be re-rendered.
 */
public interface WorldRenderExtensions {
	static void scheduleBlockRerender(World world, BlockPos pos) {
		((WorldRenderExtensions) world).scheduleBlockRerender(pos);
	}

	void scheduleBlockRerender(BlockPos pos);
}
