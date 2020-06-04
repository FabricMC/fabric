package net.fabricmc.fabric.api.fluid.v1.container.volume;

import java.util.Objects;

import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundTag;

import net.fabricmc.fabric.api.fluid.v1.Action;
import net.fabricmc.fabric.api.fluid.v1.container.FluidContainer;

public class FluidVolumeDelegate implements FluidVolume {
	public final FluidVolume delegate;

	public FluidVolumeDelegate(FluidVolume delegate) {
		this.delegate = delegate;
	}

	@Override
	public long getAmount() {
		return this.delegate.getAmount();
	}

	@Override
	public Fluid getFluid() {
		return this.delegate.getFluid();
	}

	@Override
	public CompoundTag getData() {
		return this.delegate.getData();
	}

	@Override
	public FluidVolume drain(Fluid fluid, long amount, Action action) {
		return this.delegate.drain(fluid, amount, action);
	}

	@Override
	public FluidVolume consume(FluidVolume volume, Action action) {
		return this.delegate.consume(volume, action);
	}

	@Override
	public boolean isEmpty() {
		return this.delegate.isEmpty();
	}

	@Override
	public boolean isImmutable() {
		return this.delegate.isImmutable();
	}

	@Override
	public String toString() {
		return "FluidVolumeDelegate{" + "delegate=" + this.delegate + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof FluidContainer)) return false;

		FluidContainer volume = (FluidContainer) o;
		return Objects.equals(this.delegate, volume);
	}

	@Override
	public int hashCode() {
		return this.delegate != null ? this.delegate.hashCode() : 0;
	}
}
