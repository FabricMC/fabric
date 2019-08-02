package net.fabric.test;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;

public class FabricContentRegistriesTest implements ModInitializer {

	@Override
	public void onInitialize() {
		FlammableBlockRegistry.getDefaultInstance().add(Blocks.STONE, 100, 100);
		FlammableBlockRegistry.getDefaultInstance().remove(Blocks.OAK_PLANKS);

		FuelRegistry.INSTANCE.add(Items.APPLE, 200);
		FuelRegistry.INSTANCE.remove(Blocks.OAK_PLANKS);
	}
}
