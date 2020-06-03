package net.fabricmc.fabric.api.fluids.v1.container.volume;

import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundTag;

import net.fabricmc.fabric.Action;

public class FixedSizeFluidVolume extends SimpleFluidVolume {
	private final long size;

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

	private static FluidVolume with(FluidVolume volume, long amount) {
		return new SimpleFluidVolume(volume.fluid(), amount, volume.data());
	}

	@Override
	public FluidVolume consume(FluidVolume volume, Action action) {
		return super.consume(new FluidVolumeDelegate(volume) {
			// middle-man to prevent trying to drain more-than-capacity amount of fluid
			@Override
			public FluidVolume drain(Fluid fluid, long amount, Action action) {
				return super.drain(fluid, Math.min(amount, FixedSizeFluidVolume.this.size - this.amount()), action);
			}
		}, action);
	}
}
