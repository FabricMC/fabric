package net.fabricmc.fabric.test.test;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gametest.FabricGameTestRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.test.TestFunctions;
import net.minecraft.util.math.BlockPos;

public class TestApiTestmod implements ModInitializer {
	public static final String MOD_ID = "fabric-game-test-api-v1-testmod";

	@Override
	public void onInitialize() {
		FabricGameTestRegistry.register(ExampleTestSuite.class, MOD_ID);
	}

	public static class ExampleTestSuite {

		// You can omit structureName and it will use the method name
		@GameTest(structureName = "example")
		public void example(TestContext context) {
			System.out.println("Hello from an example test");

			context.checkBlock(new BlockPos(0, 0, 0), (block) -> block == Blocks.DIAMOND_BLOCK, "Expect block to be diamond");
		}
	}
}
