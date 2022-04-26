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

package net.fabricmc.fabric.test.gametest;

import net.minecraft.block.Blocks;
import net.minecraft.structure.StructureSetKeys;
import net.minecraft.structure.StructureSets;
import net.minecraft.test.GameTest;
import net.minecraft.test.StructureTestUtil;
import net.minecraft.test.TestContext;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;

import java.io.FileNotFoundException;

public class ExampleTestSuite {
	@GameTest
	public void diamond(TestContext context) {
		context.addInstantFinalTask(() ->
				context.checkBlock(new BlockPos(0, 2, 0), (block) -> block == Blocks.DIAMOND_BLOCK, "Expect block to be diamond")
		);
	}

	@GameTest(structureName = FabricGameTest.EMPTY_STRUCTURE)
	public void noStructure(TestContext context) {
		context.setBlockState(0, 2, 0, Blocks.DIAMOND_BLOCK);

		context.addInstantFinalTask(() ->
				context.checkBlock(new BlockPos(0, 2, 0), (block) -> block == Blocks.DIAMOND_BLOCK, "Expect block to be diamond")
		);
	}

	@GameTest
	public void diamondFallback(TestContext context) {
		// As gametest/structures/exampletestsuite.diamondfallback.snbt does not exist we will fall back to `.nbt`
		context.addInstantFinalTask(() ->
				context.checkBlock(new BlockPos(0, 2, 0), (block) -> block == Blocks.DIAMOND_BLOCK, "Expect block to be diamond")
		);
	}

	@GameTest(structureName = "minecraft:igloo/top")
	public void vanillaStructure(TestContext context) {
		// using a vanilla structure to check
		context.addInstantFinalTask(() ->
				context.checkBlock(new BlockPos(4, 1, 0), (block) -> block == Blocks.SNOW_BLOCK, "Expect block to be snow")
		);
	}

	@GameTest(structureName = FabricGameTest.EMPTY_STRUCTURE)
	public void invalidStructure(TestContext context) {
		String structure = "fabric-gametest-api-v1-testmod:this_structure/does_not_exist";
		try {
			StructureTestUtil.createStructure(structure, BlockPos.ORIGIN, BlockRotation.NONE, 0, context.getWorld(), false);
		} catch (Exception e) {
			//noinspection ConstantConditions
			if(e instanceof FileNotFoundException) {
				context.complete();
				return;
			}
		}
		context.throwGameTestException("Expect structure was not found");
	}
}
