package net.fabricmc.fabric.api.fluids.v1.container;

import java.util.Arrays;
import java.util.Iterator;

import com.google.common.collect.Iterables;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.fluids.v1.container.volume.FluidVolume;
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
	public FluidVolume drain(Identifier fluid, long amount, boolean simulate) {
		FluidVolume current = new SimpleFluidVolume();

		for (FluidContainer container : this.containers) {
			FluidVolume drained = container.drain(current.isEmpty() ? fluid : current.fluid(), amount, false);
			current.add(drained, false);
			amount -= drained.amount();

			if (amount <= 0) {
				break;
			}
		}

		return current;
	}

	@Override
	public FluidVolume add(FluidVolume volume, boolean simulate) {
		for (FluidContainer container : this.containers) {
			volume = container.add(volume, simulate);

			if (volume.isEmpty()) {
				break;
			}
		}

		return volume;
	}

	@Override
	public Iterator<FluidVolume> iterator() {
		Iterator<FluidContainer> containers = this.containers.iterator();
		return new Iterator<FluidVolume>() {
			private Iterator<FluidVolume> current;

			@Override
			public boolean hasNext() {
				return (this.current != null && this.current.hasNext()) || containers.hasNext();
			}

			@Override
			public FluidVolume next() {
				if (this.current == null) {
					this.current = containers.next().iterator();
				}

				while (!this.current.hasNext()) {
					this.current = containers.next().iterator();
				}

				return this.current.next();
			}
		};
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
