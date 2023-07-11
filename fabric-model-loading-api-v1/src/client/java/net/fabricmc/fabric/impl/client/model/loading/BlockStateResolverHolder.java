package net.fabricmc.fabric.impl.client.model.loading;

import net.fabricmc.fabric.api.client.model.loading.v1.BlockStateResolver;

import net.minecraft.block.Block;
import net.minecraft.util.Identifier;

public record BlockStateResolverHolder(BlockStateResolver resolver, Block block, Identifier blockId) {
}
