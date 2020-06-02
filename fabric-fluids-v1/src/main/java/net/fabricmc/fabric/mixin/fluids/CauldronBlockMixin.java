package net.fabricmc.fabric.mixin.fluids;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.CauldronBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.fluids.v1.container.FluidContainer;
import net.fabricmc.fabric.api.fluids.v1.minecraft.blocks.CauldronFluidVolume;
import net.fabricmc.fabric.api.fluids.v1.world.SidedFluidContainer;

@Mixin (CauldronBlock.class)
public class CauldronBlockMixin implements SidedFluidContainer {
	@Override
	public FluidContainer getContainer(World world, BlockPos pos, Direction direction) {
		return new CauldronFluidVolume(world, pos);
	}
}
