package net.fabricmc.fabric.api.fluids.v1.container.volume;

import java.util.Iterator;

import com.google.common.collect.Iterators;

import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundTag;

import net.fabricmc.fabric.api.fluids.v1.container.FluidContainer;

/**
 * a fluid container that can only hold 1 fluid.
 */
public interface FluidVolume extends FluidContainer {
	@Override
	default long getTotalVolume() {
		return this.getAmount();
	}

	/**
	 * @return the amount of fluid currently in the fluid volume.
	 */
	long getAmount();

	@Override
	default Iterator<FluidVolume> iterator() {
		return Iterators.singletonIterator(this);
	}

	@Override
	default FluidVolume simpleCopy() {
		return new SimpleFluidVolume(this.getFluid(), this.getAmount(), this.getData() == null ? null : this.getData().copy());
	}

	/**
	 * @return the id for the fluid
	 */
	Fluid getFluid();

	/**
	 * @return the data associated with the fluid
	 */
	CompoundTag getData();
}
