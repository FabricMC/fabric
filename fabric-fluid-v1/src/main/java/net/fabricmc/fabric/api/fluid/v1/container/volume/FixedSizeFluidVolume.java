package net.fabricmc.fabric.api.fluid.v1.container.volume;

import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundTag;

import net.fabricmc.fabric.api.fluid.v1.Action;

public class FixedSizeFluidVolume extends SimpleFluidVolume {
	public final long size;

	public FixedSizeFluidVolume(Fluid fluid, long amount, CompoundTag data, long size) {
		super(fluid, amount, data);
		this.size = size;
	}

	public FixedSizeFluidVolume(Fluid fluid, long amount, long size) {
		super(fluid, amount);
		this.size = size;
	}

	public FixedSizeFluidVolume(long size) {
		this.size = size;
	}

	public FixedSizeFluidVolume(CompoundTag tag, long size) {
		super(tag);
		this.size = size;
	}

	@Override
	public FluidVolume consume(FluidVolume volume, Action action) {
		FluidVolumeDelegate delegate = new FluidVolumeDelegate(volume) {
			// middle-man to prevent trying to drain more-than-capacity amount of fluid
			@Override
			public FluidVolume drain(Fluid fluid, long amount, Action action) {
				return super.drain(fluid, Math.min(amount, FixedSizeFluidVolume.this.size - FixedSizeFluidVolume.this.getAmount()), action);
			}

		};
		FluidVolume vol = super.consume(delegate, action);
		return vol == delegate ? delegate.delegate : vol;
	}
}
