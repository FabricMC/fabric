package net.fabricmc.fabric.test.object.builder;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import static net.fabricmc.fabric.test.object.builder.ObjectBuilderTestConstants.id;

public class BlockSettingsTest implements ModInitializer {
	@Override
	public void onInitialize() {
		// This block should not cause a crash on a dedicated server
		Registry.register(Registry.BLOCK, id("cutout_block"), new Block(FabricBlockSettings.of(Material.STONE).nonOpaque().cutout()));
	}
}
