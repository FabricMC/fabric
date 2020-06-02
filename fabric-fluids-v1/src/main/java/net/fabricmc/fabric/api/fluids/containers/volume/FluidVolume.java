package net.fabricmc.fabric.api.fluids.containers.volume;

import java.util.Iterator;

import com.google.common.collect.Iterators;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.fluids.containers.FluidContainer;

/**
 * a fluid container that can only hold 1 fluid
 */
public interface FluidVolume extends FluidContainer {

	/**
	 * @return the id for the fluid
	 */
	Identifier fluid();

	/**
	 * @return the data associated with the fluid
	 */
	CompoundTag data();

	@Override
	default Iterator<FluidVolume> iterator() {
		return Iterators.singletonIterator(this);
	}

	@Override
	default long getTotalVolume() {
		return this.amount();
	}

	/**
	 * @return the amount of fluid currently in the fluid volume
	 */
	long amount();
}
