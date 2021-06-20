package net.fabricmc.fabric.test.tree.v1;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

/**
 * A simple trunk placer placing two straight trunks alongside each other.
 */
public class BasicTrunkPlacer extends TrunkPlacer {
	// A simple codec with an additional entry as an example
	// Your codecs should typically be contained inside of the class as a static field
	public static final Codec<BasicTrunkPlacer> CODEC = RecordCodecBuilder
			.create((instance) ->
					fillTrunkPlacerFields(instance)
					.and(Codec.INT
							.fieldOf("customIntValue")
							.forGetter((self) -> self.customIntValue))
					.apply(instance, BasicTrunkPlacer::new));

	// This property is to demonstrate adding your own constructor arguments to your codec.
	// Creating codecs for custom data types is an advanced topic and won't be covered in this testmod.
	private final int customIntValue;

	public BasicTrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight, int customIntValue) {
		super(baseHeight, firstRandomHeight, secondRandomHeight);

		this.customIntValue = customIntValue;
	}

	@Override
	protected TrunkPlacerType<?> getType() {
		return TreeTest.BASIC_TRUNK_PLACER;
	}

	@Override
	public List<FoliagePlacer.TreeNode> generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config) {
		// Set the block beneath the trunk to dirt
		setToDirt(world, replacer, random, startPos.down(), config);

		// Iterate to the max trunk height
		for (int i = 0; i < height; i++) {
			// Place two trunk blocks alongside each other using the built-in getAndSetState method
			// BlockPos has really good convenience methods like up, offset, east, west etc. that can also be combined together
			getAndSetState(world, replacer, random, startPos.up(i), config);
			getAndSetState(world, replacer, random, startPos.up(i).east(), config);
		}

		// Every TreeNode is a part of your trunk
		// For performance reasons, using an ImmutableList from the google.common.collect library is better than an ArrayList
		// The arguments represent the max height of this TreeNode, the foliage radius and is this a giant trunk (like 2-block diameter trees from vanilla)
		return ImmutableList.of(new FoliagePlacer.TreeNode(startPos.up(height), 0, false),
								new FoliagePlacer.TreeNode(startPos.east().up(height), 0, false));
	}
}
