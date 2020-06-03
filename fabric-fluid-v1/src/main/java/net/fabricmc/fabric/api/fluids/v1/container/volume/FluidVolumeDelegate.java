package net.fabricmc.fabric.api.fluids.v1.container.volume;

import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundTag;

import net.fabricmc.fabric.Action;

public class FluidVolumeDelegate implements FluidVolume {
	private final FluidVolume delegate;

	public FluidVolumeDelegate(FluidVolume delegate) {this.delegate = delegate;}

	@Override
	public long getAmount() {return this.delegate.getAmount();}

	@Override
	public Fluid getFluid() {return this.delegate.getFluid();}

	@Override
	public CompoundTag getData() {return this.delegate.getData();}

	@Override
	public FluidVolume drain(Fluid fluid, long amount, Action action) {return this.delegate.drain(fluid, amount, action);}

	@Override
	public FluidVolume consume(FluidVolume volume, Action action) {return this.delegate.consume(volume, action);}

	@Override
	public boolean isEmpty() {return this.delegate.isEmpty();}

	@Override
	public boolean isImmutable() {return this.delegate.isImmutable();}
}
