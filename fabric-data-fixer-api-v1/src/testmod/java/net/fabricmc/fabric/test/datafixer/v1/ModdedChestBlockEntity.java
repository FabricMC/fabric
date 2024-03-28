package net.fabricmc.fabric.test.datafixer.v1;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.util.math.BlockPos;

public class ModdedChestBlockEntity extends ChestBlockEntity {
	protected ModdedChestBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
	}

	protected ModdedChestBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}
}
