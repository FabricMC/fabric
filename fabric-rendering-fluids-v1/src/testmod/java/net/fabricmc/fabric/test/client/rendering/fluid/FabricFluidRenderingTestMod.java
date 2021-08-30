package net.fabricmc.fabric.test.client.rendering.fluid;

import net.fabricmc.api.ModInitializer;

public class FabricFluidRenderingTestMod implements ModInitializer {
	@Override
	public void onInitialize() {
		new TestFluids();
	}
}
