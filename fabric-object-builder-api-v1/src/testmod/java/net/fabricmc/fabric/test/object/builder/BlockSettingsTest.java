package net.fabricmc.fabric.test.object.builder;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

public class BlockSettingsTest implements ModInitializer {
	public static final Block TEST_BLOCK_1 = new Block(FabricBlockSettings.of(Material.WOOD).pistonBehavior(PistonBehavior.DESTROY).replaceable(true).solid(false));
	public static final Block TEST_BLOCK_2 = new Block(FabricBlockSettings.of(Material.AIR).pistonBehavior(PistonBehavior.PUSH_ONLY).replaceable(false).solid(true));

	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, ObjectBuilderTestConstants.id("test_block_1"), TEST_BLOCK_1);
		Registry.register(Registry.BLOCK, ObjectBuilderTestConstants.id("test_block_2"), TEST_BLOCK_2);
	}
}
