package net.fabricmc.fabric.test.tree.v1;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

/**
 * A simple tree decorator placing a gold block on a random side of the tree's trunk with a 3% chance
 */
public class BasicTreeDecorator extends TreeDecorator {
	// Tree decorators don't have anything in their codec by default, so without any arguments your codec would look like:
	// public static final Codec<BasicTreeDecorator> CODEC = Codec.unit((instance) -> new BasicTreeDecorator());

	// In our case we add three new values and, instead of using a base method like fillTrunkPlacerFields, we use an instance.group call and put our entries in it
	// Apart from that, the process isn't much different:
	public static final Codec<BasicTreeDecorator> CODEC = RecordCodecBuilder
			.create((instance) ->
					instance.group(
						// You can put up to 16 codec entries inside (inclusive), but the source code for the .group5 method is ugly as hell
						// For more than 16 entries, use .and calls after the .group call and before the .apply call
						Codec.INT
							.fieldOf("customIntValue")
							.forGetter((self) -> self.customIntValue),
						Codec.FLOAT
							.fieldOf("customFloatValue")
							.forGetter((self) -> self.customFloatValue),
						Codec.STRING
							.fieldOf("customStringValue")
							.forGetter((self) -> self.customStringValue))
					.apply(instance, BasicTreeDecorator::new));

	private final int customIntValue;
	private final float customFloatValue;
	private final String customStringValue;

	public BasicTreeDecorator(int customIntValue, float customFloatValue, String customStringValue) {
		this.customIntValue = customIntValue;
		this.customFloatValue = customFloatValue;
		this.customStringValue = customStringValue;
	}

	@Override
	protected TreeDecoratorType<?> getType() {
		return TreeTest.BASIC_TREE_DECORATOR;
	}

	@Override
	public void generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions) {
		// We can use the logPositions argument to iterate through every single trunk log
		for (BlockPos logPosition : logPositions) {
			// Only proceed if we're lucky and the 3% chance actually worked
			if (random.nextInt(101) > 3) continue;

			// Use an enhanced switch (1.17+ mods use Java 16, so it's okay as long as the language level is max stable)
			// to determine the side where the gold block will be placed
			Direction side = switch (random.nextInt(4)) {
				case 0 -> Direction.NORTH;
				case 1 -> Direction.SOUTH;
				case 2 -> Direction.EAST;
				case 3 -> Direction.WEST;
				default -> throw new ArithmeticException("Something went seriously wrong and the picked value is out of bounds");
			};

			// Place the gold block at the log position + offset at the side
			// If there aren't any built-in block placing methods, use the replacer BiConsumer
			replacer.accept(logPosition.offset(side, 1), Blocks.GOLD_BLOCK.getDefaultState());
		}
	}
}
