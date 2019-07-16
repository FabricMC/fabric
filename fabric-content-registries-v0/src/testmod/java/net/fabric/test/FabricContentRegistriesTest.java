package net.fabric.test;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.Blocks;

public class FabricContentRegistriesTest implements ModInitializer {

	@Override
	public void onInitialize() {
		FlammableBlockRegistry.getDefaultInstance().add(Blocks.STONE, 100, 100);
		FlammableBlockRegistry.getDefaultInstance().remove(Blocks.OAK_PLANKS);
	}
}
