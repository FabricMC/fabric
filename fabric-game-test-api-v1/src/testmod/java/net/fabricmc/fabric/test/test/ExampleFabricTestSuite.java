package net.fabricmc.fabric.test.test;

import java.lang.reflect.Method;

import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;

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
				context.checkBlock(new BlockPos(0, 5, 0), (block) -> block == Blocks.GOLD_BLOCK, "Expect block to be gold")
		);
	}

	@GameTest(structureName = "fabric-game-test-api-v1-testmod:exampletestsuite.diamond")
	public void diamond(TestContext context) {
		context.addInstantFinalTask(() ->
				context.checkBlock(new BlockPos(0, 2, 0), (block) -> block == Blocks.DIAMOND_BLOCK, "Expect block to be diamond")
		);
	}
}
