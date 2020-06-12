package net.fabricmc.fabric.test.structures;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.structures.v0.FabricStructures;
import net.fabricmc.fabric.test.structures.structure.TestStructure;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

/**
 * Test mod for the structures module
 */
public class FabricStructuresTest implements ModInitializer {

	public static final StructureFeature<DefaultFeatureConfig> TEST_STRUCTURE = FabricStructures.register(new Identifier("fabric", "test_structure"), new TestStructure());

	@Override
	public void onInitialize() {
		Registry.BIOME.forEach((biome) -> biome.addStructureFeature(TEST_STRUCTURE.configure(new DefaultFeatureConfig())));
	}
}
