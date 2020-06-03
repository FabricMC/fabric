package net.fabricmc.fabric.api.fluids.v1.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import net.fabricmc.fabric.Action;
import net.fabricmc.fabric.api.fluids.v1.container.FluidContainer;
import net.fabricmc.fabric.api.fluids.v1.container.volume.FluidVolume;
import net.fabricmc.fabric.api.fluids.v1.container.volume.SimpleFluidVolume;
import net.fabricmc.fabric.api.fluids.v1.math.Drops;

/**
 * a container with different fluid containers per face, can be implemented on entities, and blocks (does not check block entities!).
 */
public interface SidedFluidContainer extends FluidFillable, FluidDrainable {
	@Override
	default Fluid tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state) {
		if(world instanceof World) {
			FluidVolume volume = this.getContainer((World) world, pos, null).drain(Drops.getBucket(), Action.SIMULATE);
			if(volume.fluid() != Fluids.EMPTY && volume.amount() == Drops.getBucket()) {
				return this.getContainer((World) world, pos, null).drain(Drops.getBucket(), Action.PERFORM).fluid();
			}
		}
		return Fluids.EMPTY;
	}

	/**
	 * get the container for the given face.
	 *
	 * @param world the world
	 * @param pos the position
	 * @param direction the face being accessed, nullable
	 * @return the container
	 */
	FluidContainer getContainer(World world, BlockPos pos, Direction direction);

	@Override
	default boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
		if(world instanceof World) {
			return this.getContainer((World)world, pos, null).consume(new SimpleFluidVolume(fluid, Drops.getBucket()), Action.SIMULATE).amount() == Drops.getBucket();
		}
		return false;
	}

	@Override
	default boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
		if(this.canFillWithFluid(world, pos, state, fluidState.getFluid())) {
			this.getContainer((World)world, pos, null).consume(new SimpleFluidVolume(fluidState.getFluid(), Drops.getBucket()), Action.PERFORM);
			return true;
		}
		return false;
	}
}
