package net.fabricmc.fabric.api.fluids.v1.container;

import java.util.Arrays;
import java.util.Iterator;

import com.google.common.collect.Iterables;

import net.minecraft.fluid.Fluid;

import net.fabricmc.fabric.Action;
import net.fabricmc.fabric.api.fluids.v1.container.volume.FluidVolume;
import net.fabricmc.fabric.api.fluids.v1.container.volume.ImmutableFluidVolume;
import net.fabricmc.fabric.api.fluids.v1.container.volume.SimpleFluidVolume;

public class SimpleFluidContainer implements FluidContainer {
	private final Iterable<FluidContainer> containers;

	public SimpleFluidContainer(FluidContainer... containers) {
		this(Arrays.asList(containers));
	}

	public SimpleFluidContainer(Iterable<FluidContainer> containers) {
		this.containers = containers;
	}

	@Override
	public FluidVolume drain(Fluid fluid, long amount, Action simulate) {
		FluidVolume current = new SimpleFluidVolume();

		for (FluidContainer container : this.containers) {
			FluidVolume drained = container.drain(current.isEmpty() ? fluid : current.fluid(), amount, Action.SIMULATE);
			if (!current.add(drained, Action.PERFORM).equals(ImmutableFluidVolume.EMPTY)) {
				container.drain(current.isEmpty() ? fluid : current.fluid(), amount, simulate);
			}
			amount -= drained.amount();

			if (amount <= 0) {
				break;
			}
		}

		return current;
	}

	@Override
	public FluidVolume add(FluidVolume volume, Action action) {
		for (FluidContainer container : this.containers) {
			volume = container.add(volume, action);

			if (volume.isEmpty()) {
				break;
			}
		}

		return volume;
	}

	@Override
	public Iterator<FluidVolume> iterator() {
		return Iterables.concat(this.containers).iterator();
	}

	@Override
	public boolean isEmpty() {
		for (FluidContainer container : this.containers) {

			if (!container.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public long getTotalVolume() {
		long total = 0;

		for (FluidContainer container : this.containers) {
			total += container.getTotalVolume();
		}

		return total;
	}

	@Override
	public boolean isImmutable() {
		return false;
	}

	@Override
	public int hashCode() {
		return this.containers != null ? this.containers.hashCode() : 0;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof FluidContainer)) return false;

		FluidContainer that = (FluidContainer) o;

		return Iterables.elementsEqual(this.containers, that);
	}
}
