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

package net.fabricmc.fabric.test.dimension.gametest;

import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;

// Test raycasting without an entity.
public class RaycastGameTest implements FabricGameTest {
	@GameTest(templateName = EMPTY_STRUCTURE)
	public void testRaycast(TestContext context) {
		final BlockPos diamondPos = BlockPos.ORIGIN.up(3);
		context.setBlockState(diamondPos, Blocks.DIAMOND_BLOCK.getDefaultState());

		var raycastContext = new RaycastContext(context.getAbsolute(Vec3d.ZERO.add(0, 1, 0)), context.getAbsolute(Vec3d.ZERO.add(0, 5, 0)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, null);
		BlockHitResult blockHitResult = context.getWorld().raycast(raycastContext);

		if (!context.getAbsolutePos(diamondPos).equals(blockHitResult.getBlockPos())) {
			context.throwGameTestException("Positions do not match");
		}

		context.complete();
	}
}
