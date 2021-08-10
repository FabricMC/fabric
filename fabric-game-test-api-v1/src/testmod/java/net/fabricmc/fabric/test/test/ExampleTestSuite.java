package net.fabricmc.fabric.test.test;

import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;

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
}
