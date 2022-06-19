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

package net.fabricmc.fabric.test.content.registry;

import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.GameTestException;
import net.minecraft.test.TestContext;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;

public class FlammableTest {
	/**
	 * Regression test for <a href="https://github.com/FabricMC/fabric/issues/2108">FlammableBlockRegistry ignoring tags on first load</a>.
	 */
	@GameTest(structureName = FabricGameTest.EMPTY_STRUCTURE)
	public void testFlammableTag(TestContext context) {
		if (FlammableBlockRegistry.getDefaultInstance().get(Blocks.SAND).getBurnChance() != 4) {
			throw new GameTestException("Expected blocks in the sand tag to be flammable!");
		}

		context.complete();
	}
}
