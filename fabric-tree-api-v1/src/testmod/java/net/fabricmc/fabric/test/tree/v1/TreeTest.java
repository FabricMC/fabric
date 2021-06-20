package net.fabricmc.fabric.test.tree.v1;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.tree.v1.FabricTreeRegistry;
import net.fabricmc.fabric.api.tree.v1.SimpleSaplingBlock;
import net.fabricmc.fabric.api.tree.v1.SimpleSaplingGenerator;
import net.minecraft.block.Blocks;
import net.minecraft.block.SaplingBlock;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProviderType;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

import java.util.Collections;

/**
 * A test mod for the Fabric Tree API v1
 */
public class TreeTest implements ModInitializer {
	public static final String MOD_ID = "fabric-tree-api-v1-testmod";

	// Types
	public static final TrunkPlacerType<BasicTrunkPlacer> BASIC_TRUNK_PLACER =
			FabricTreeRegistry.registerTrunkPlacerType(new Identifier(MOD_ID, "basic_trunk_placer"), BasicTrunkPlacer.CODEC);
	public static final FoliagePlacerType<BasicFoliagePlacer> BASIC_FOLIAGE_PLACER =
			FabricTreeRegistry.registerFoliagePlacerType(new Identifier(MOD_ID, "basic_foliage_placer"), BasicFoliagePlacer.CODEC);
	public static final TreeDecoratorType<BasicTreeDecorator> BASIC_TREE_DECORATOR =
			FabricTreeRegistry.registerTreeDecoratorType(new Identifier(MOD_ID, "basic_tree_decorator"), BasicTreeDecorator.CODEC);
	public static final BlockStateProviderType<BasicBlockStateProvider> BASIC_BLOCK_STATE_PROVIDER =
			FabricTreeRegistry.registerBlockStateProviderType(new Identifier(MOD_ID, "basic_block_state_provider"), BasicBlockStateProvider.CODEC);
	public static final IntProviderType<BasicIntProvider> BASIC_INT_PROVIDER =
			FabricTreeRegistry.registerIntProviderType(new Identifier(MOD_ID, "basic_int_provider"), BasicIntProvider.CODEC);

	// Sapling
	public static final SaplingBlock BASIC_SAPLING = new SimpleSaplingBlock(new SimpleSaplingGenerator(BASIC_TREE), FabricBlockSettings.copyOf(Blocks.OAK_SAPLING));

	// Tree ConfiguredFeature
	public static final ConfiguredFeature<?, ?> BASIC_TREE = Feature.TREE
			.configure(new TreeFeatureConfig.Builder(
					new SimpleBlockStateProvider(Blocks.BIRCH_LOG.getDefaultState()),
					new BasicTrunkPlacer(8, 3, 0, 69420),
					new BasicBlockStateProvider(Registry.BLOCK.getRawId(Blocks.BIRCH_LEAVES), Registry.BLOCK.getRawId(Blocks.OAK_LEAVES)),
					new SimpleBlockStateProvider(BASIC_SAPLING.getDefaultState()),
					new BasicFoliagePlacer(ConstantIntProvider.create(5), ConstantIntProvider.create(0), ConstantIntProvider.create(3)),
					new TwoLayersFeatureSize(1, 0, 1)
			).decorators(Collections.singletonList(new BasicTreeDecorator(69420, 69.420f, "69420"))).build())

			.decorate(Decorator.CHANCE.configure(new ChanceDecoratorConfig(30)));

	@Override
	public void onInitialize() {
		// Register the tree ConfiguredFeature and add it to all biomes as vegetal decoration
		RegistryKey<ConfiguredFeature<?, ?>> key = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY, new Identifier(MOD_ID, "basic_tree"));

		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, key.getValue(), BASIC_TREE);

		BiomeModifications.addFeature(BiomeSelectors.all(), GenerationStep.Feature.VEGETAL_DECORATION, key);
	}
}
