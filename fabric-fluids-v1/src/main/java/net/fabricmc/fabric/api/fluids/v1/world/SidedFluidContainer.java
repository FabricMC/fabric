package net.fabricmc.fabric.api.fluids.v1.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.fluids.v1.container.FluidContainer;

/**
 * a container with different fluid containers per face, can be implemented on entities, and blocks (does not check block entities!).
 */
public interface SidedFluidContainer {
	/**
	 * get the container for the given face.
	 *
	 * @param world the world
	 * @param pos the position
	 * @param direction the face being accessed
	 * @return the container
	 */
	FluidContainer getContainer(World world, BlockPos pos, Direction direction);
}
