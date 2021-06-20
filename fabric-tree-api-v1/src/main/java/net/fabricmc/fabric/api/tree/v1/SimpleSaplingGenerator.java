package net.fabricmc.fabric.api.tree.v1;

import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * <p>A simple implementation of a {@link SaplingGenerator} returning the tree {@link ConfiguredFeature} given to it.</p>
 * <p>Most of the time when working with {@link SaplingGenerator}s, you'll only need this functionality.</p>
 * <p>
 *     An example of using this implementation:<br>
 *
 *     <code>
 *         public static final MY_SAPLING_GENERATOR = new SimpleSaplingGenerator(MY_TREE_CONFIGURED);
 *     </code>
 * </p>
 */
public class SimpleSaplingGenerator extends SaplingGenerator {
	/**
	 * The stored and returned {@link ConfiguredFeature}
	 */
	private final ConfiguredFeature<?, ?> treeFeature;

	public SimpleSaplingGenerator(ConfiguredFeature<?, ?> treeFeature) {
		this.treeFeature = treeFeature;
	}

	@Nullable
	@Override
	protected ConfiguredFeature<TreeFeatureConfig, ?> createTreeFeature(Random random, boolean bees) {
		// Unfortunately, this dirty cast is necessary here to support decorated tree ConfiguredFeatures
		return (ConfiguredFeature<TreeFeatureConfig, ?>) treeFeature;
	}
}
