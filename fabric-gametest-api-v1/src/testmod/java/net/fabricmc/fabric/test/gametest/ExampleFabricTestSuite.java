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

import java.lang.reflect.Method;

import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;

// optional to impl FabricGameTest
public class ExampleFabricTestSuite implements FabricGameTest {
	/**
	 * By overriding invokeTestMethod you can wrap the method call.
	 * This can be used as shown to run code before and after each test.
	 */
	@Override
	public void invokeTestMethod(TestContext context, Method method) {
		beforeEach(context);

		FabricGameTest.super.invokeTestMethod(context, method);

		afterEach(context);
	}

	private void beforeEach(TestContext context) {
		System.out.println("Hello beforeEach");
		context.setBlockState(0, 5, 0, Blocks.GOLD_BLOCK);
	}

	private void afterEach(TestContext context) {
		context.addInstantFinalTask(() ->
				context.checkBlock(new BlockPos(0, 2, 0), (block) -> block == Blocks.DIAMOND_BLOCK, "Expect block to be gold")
		);
	}

	@GameTest(structureName = "fabric-gametest-api-v1-testmod:exampletestsuite.diamond")
	public void diamond(TestContext context) {
		// Nothing to do as the structure placed the block.
	}

	@GameTest(structureName = EMPTY_STRUCTURE)
	public void noStructure(TestContext context) {
		context.setBlockState(0, 2, 0, Blocks.DIAMOND_BLOCK);
	}
}
