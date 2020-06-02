package net.fabricmc.fabric.api.fluids.containers;

import java.util.Iterator;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.fluids.containers.volume.FluidVolume;
import net.fabricmc.fabric.api.fluids.minecraft.FluidIds;

/**
 * An object that stores fluid, it is assumed that all fluid containers that only hold one fluid fluid Volumes
 */
public interface FluidContainer extends Iterable<FluidVolume> {
	default FluidVolume drain(long amount, boolean simulate) {
		return this.drain(FluidIds.EMPTY, amount, simulate);
	}

	/**
	 * drain an amount of fluid from the container
	 *
	 * @param fluid the specific fluid to drain, or EMPTY for wildcard
	 * @param amount the amount of fluid in drops
	 * @param simulate true if the state of the container should not mutate
	 * @return the amount of fluid actually drained
	 */
	FluidVolume drain(Identifier fluid, long amount, boolean simulate);

	/**
	 * add an amount of fluid to the container
	 *
	 * @param container the amount to be added
	 * @param simulate true if the state of the container should not mutate
	 * @return the amount left over
	 */
	FluidVolume add(FluidVolume container, boolean simulate);

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
