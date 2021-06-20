package net.fabricmc.fabric.test.tree.v1;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

import java.util.Random;
import java.util.function.BiConsumer;

/**
 * A simple foliage placer creating a cube out of foliage on top of your trunk.
 */
public class BasicFoliagePlacer extends FoliagePlacer {
	// This codec is very similar to the trunk placer codec, but it uses the codec created by IntProvider.createValidatingCodec
	public static final Codec<BasicFoliagePlacer> CODEC = RecordCodecBuilder
			.create((instance) ->
					fillFoliagePlacerFields(instance)
					// The arguments for the createValidatingCodec method are the min and max values of the provider
					// Typically, it's from 0 to 256
					// The rest of the .and call stays the same
					.and(IntProvider.createValidatingCodec(0, 256)
							.fieldOf("height")
							.forGetter((self) -> self.height))
					.apply(instance, BasicFoliagePlacer::new));

	// This is another example of adding a value to your codec
	private IntProvider height;

	public BasicFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider height) {
		super(radius, offset);

		this.height = height;
	}

	@Override
	protected FoliagePlacerType<?> getType() {
		return TreeTest.BASIC_FOLIAGE_PLACER;
	}

	@Override
	protected void generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, TreeFeatureConfig config, int trunkHeight, TreeNode treeNode, int foliageHeight, int radius, int offset) {
		// Retrieve the center position of the generated trunk
		BlockPos.Mutable center = treeNode.getCenter().mutableCopy();

		// Create the cube
		for (int y = center.getY() - radius; y <= center.getY() + radius; y++) {
			for (int z = center.getZ() - radius; z <= center.getZ() + radius; z++) {
				for (int x = center.getX() - radius; x <= center.getX() + radius; x++) {
					// Place the block using placeFoliageBlock method
					placeFoliageBlock(world, replacer, random, config, new BlockPos(x, y, z));
				}
			}
		}
	}

	@Override
	public int getRandomHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
		// Just return the value picked by your height provider
		return height.get(random);
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
		// Here you can set restrictions for generating the foliage depending on the given arguments
		// Our FoliagePlacer doesn't set any
		return false;
	}
}
