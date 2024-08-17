package net.fabricmc.fabric.api.object.builder.v1.block.entity;

import java.util.Set;
import java.util.function.BiFunction;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class FabricBlockEntityTypeBuilder {
	public static <T extends BlockEntity> BlockEntityType<T> create(BiFunction<BlockPos, BlockState, T> factory, Block... blocks) {
		return new BlockEntityType<>(factory::apply, Set.of(blocks));
	}
}
