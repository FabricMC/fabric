package net.fabricmc.fabric.test.transfer.fluid;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class FluidChuteBlock extends Block implements BlockEntityProvider {
	public FluidChuteBlock() {
		super(Settings.of(Material.METAL));
	}

	@Override
	public @Nullable BlockEntity createBlockEntity(BlockView world) {
		return new FluidChuteBlockEntity();
	}
}
