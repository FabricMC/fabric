package net.fabricmc.fabric.api.fluid.v1.container;

import java.util.Iterator;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;

import net.fabricmc.fabric.api.fluid.v1.Action;
import net.fabricmc.fabric.api.fluid.v1.container.volume.FluidVolume;
import net.fabricmc.fabric.api.fluid.v1.container.volume.SimpleFluidVolume;

/**
 * An object that stores fluid.
 * FluidContainers can hold one or more types of fluid at once.
 *
 * @see FluidVolume
 */
public interface FluidContainer extends Iterable<FluidVolume> {
	/**
	 * Drains a wildcard matched fluid.
	 * @param amount the amount to drain
	 * @param action the nature of the transaction
	 * @return the fluid drained
	 */
	default FluidVolume drain(long amount, Action action) {
		return this.drain(Fluids.EMPTY, amount, action);
	}

	/**
	 * Drain an amount of fluid from the container.
	 *
	 * @param fluid the specific fluid to drain, or EMPTY for wildcard
	 * @param amount the amount of fluid in drops
	 * @param action the nature of the transaction
	 * @return the amount of fluid actually drained
	 */
	FluidVolume drain(Fluid fluid, long amount, Action action);

	/**
	 * Attempt to drain from all the fluid volumes inside the fluid container and insert it to this container.
	 * @param container the container
	 * @param action the nature of the transaction
	 */
	default void consume(FluidContainer container, Action action) {
		for (FluidVolume volume : container) {
			this.consume(volume, action);
		}
	}

	/**
	 * Add an amount of fluid to the container
	 * @return a newly created container representing the leftover fluid from the transaction
	 */
	default FluidVolume add(Fluid fluid, long amount, Action action) {
		return this.consume(new SimpleFluidVolume(fluid, amount), action);
	}

	/**
	 * Add an amount of fluid to the container, and drain from the one given.
	 *
	 * @param volume the amount to be added, and drained
	 * @param action the nature of the transaction
	 * @return the amount leftover, so that a simulated action's result can still be observed without modifying the container
	 */
	FluidVolume consume(FluidVolume volume, Action action);

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

	/**
	 * @return a simple copy of the object, only contains the fluid, data and amount
	 */
	FluidContainer simpleCopy();
}
