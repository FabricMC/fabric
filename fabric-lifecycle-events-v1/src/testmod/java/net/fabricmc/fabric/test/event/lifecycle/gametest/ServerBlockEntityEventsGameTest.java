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

package net.fabricmc.fabric.test.event.lifecycle.gametest;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.GameTestException;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.gametest.v1.EventSpy;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.fabric.api.gametest.v1.FabricTestContext;

public class ServerBlockEntityEventsGameTest implements FabricGameTest {
	@GameTest(templateName = EMPTY_STRUCTURE)
	public void loadBlockEntity(TestContext context) {
		// Cast not needed in a normal mod due to interface injection
		final FabricTestContext ctx = (FabricTestContext) context;

		EventSpy<ServerBlockEntityEvents.Load> spy = ctx.eventSpy(ServerBlockEntityEvents.BLOCK_ENTITY_LOAD, spyCtx -> (blockEntity, world) -> {
			// Call invoke to increment the call count
			// Returns true when this test is running, false once this test has completed
			// Passing a blockpos to ensure that we only count events triggered from within the test structure
			if (spyCtx.invoke(blockEntity.getPos())) {
				// Test is running can expect inputs
				if (!(blockEntity instanceof FurnaceBlockEntity)) {
					throw new GameTestException("Expected FurnaceBlockEntity but got %s".formatted(blockEntity.getClass().getName()));
				}
			}
		});

		context.setBlockState(BlockPos.ORIGIN.up(), Blocks.FURNACE);

		context.addFinalTask(() -> {
			// Verify that the event was called once while the test is running.
			spy.verifyCalledTimes(1);
		});
	}
}
