package net.fabricmc.fabric.impl.client.model.loading;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.client.model.loading.v1.BlockStateResolver;

import net.minecraft.block.Block;

public class BlockStateResolverRegistry {
	private static final Map<Block, BlockStateResolver> RESOLVERS = new HashMap<>();

	public static void register(Block block, BlockStateResolver resolver) {
		RESOLVERS.put(block, resolver);
	}

	@Nullable
	public static BlockStateResolver get(Block block) {
		return RESOLVERS.get(block);
	}
}
