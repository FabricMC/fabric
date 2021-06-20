package net.fabricmc.fabric.test.tree.v1;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

import java.util.Random;

/**
 * A simple block state provider returning one of the blocks passed into it
 */
public class BasicBlockStateProvider extends BlockStateProvider {
	public static final Codec<BasicBlockStateProvider> CODEC = RecordCodecBuilder
			.create((instance) ->
					instance.group(
						Codec.INT
							.fieldOf("blockRaw1")
							.forGetter((self) -> self.blockRaw1),
						Codec.INT
							.fieldOf("blockRaw2")
							.forGetter((self) -> self.blockRaw2))
					.apply(instance, BasicBlockStateProvider::new));

	private final int blockRaw1;
	private final int blockRaw2;

	// Pass the blocks using their raw registry IDs to avoid creating a custom codec which is hard to do
	public BasicBlockStateProvider(int blockRaw1, int blockRaw2) {
		this.blockRaw1 = blockRaw1;
		this.blockRaw2 = blockRaw2;
	}

	@Override
	protected BlockStateProviderType<?> getType() {
		return TreeTest.BASIC_BLOCK_STATE_PROVIDER;
	}

	@Override
	public BlockState getBlockState(Random random, BlockPos pos) {
		// Extract the blocks using the Minecraft registry
		Block block1 = Registry.BLOCK.get(blockRaw1);
		Block block2 = Registry.BLOCK.get(blockRaw2);

		// Pick a number, 0 or 1, and return block1 or block2 depending on it
		return random.nextInt(2) == 0 ? block1.getDefaultState() : block2.getDefaultState();
	}
}
