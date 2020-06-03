package net.fabricmc.fabric.api.fluids.v1.container;

import java.util.Iterator;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;

import net.fabricmc.fabric.Action;
import net.fabricmc.fabric.api.fluids.v1.container.volume.FluidVolume;

/**
 * An object that stores fluid, it is assumed that all fluid containers that only hold one fluid fluid Volumes.
 */
public interface FluidContainer extends Iterable<FluidVolume> {
	default FluidVolume drain(long amount, Action simulate) {
		return this.drain(Fluids.EMPTY, amount, simulate);
	}

	/**
	 * drain an amount of fluid from the container.
	 *
	 * @param fluid the specific fluid to drain, or EMPTY for wildcard
	 * @param amount the amount of fluid in drops
	 * @param action the nature of the transaction
	 * @return the amount of fluid actually drained
	 */
	FluidVolume drain(Fluid fluid, long amount, Action action);

	/**
	 * add an amount of fluid to the container.
	 *
	 * @param container the amount to be added
	 * @param action the nature of the transaction
	 * @return the amount left over
	 */
	FluidVolume add(FluidVolume container, Action action);

	/**
	 * @return all the individual fluid volumes that make up this fluid container or `this` if it is a fluid volume
	 */
	@Override
	Iterator<FluidVolume> iterator();

	/**
	 * @return true if the fluid container is empty
	 */
	boolean isEmpty();

	/**
	 * @return total volume of fluid this container has
	 */
	long getTotalVolume();

	/**
	 * @return true if the fluid container is immutable
	 */
	boolean isImmutable();
}
