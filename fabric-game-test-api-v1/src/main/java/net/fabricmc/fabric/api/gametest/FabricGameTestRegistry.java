package net.fabricmc.fabric.api.gametest;

import net.fabricmc.fabric.impl.client.gametest.FabricGameTestHelperImpl;

public interface FabricGameTestRegistry {
	static void register(Class<?> testClass, String modid) {
		FabricGameTestHelperImpl.register(testClass, modid);
	}
}
