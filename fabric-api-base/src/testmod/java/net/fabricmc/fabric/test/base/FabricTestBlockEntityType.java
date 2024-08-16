package net.fabricmc.fabric.test.base;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

import java.util.Set;
import java.util.function.BiFunction;

public class FabricTestBlockEntityType {
	public static <T extends BlockEntity> BlockEntityType<T> create(BiFunction<BlockPos, BlockState, T> factory, Block... blocks) {
		return null;
	}
}
