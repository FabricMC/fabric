package net.fabricmc.fabric.api.fluids.containers.volume;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.fluids.minecraft.FluidIds;
import net.fabricmc.fabric.api.fluids.properties.FluidPropertyMerger;

public class FixedSizeFluidVolume extends SimpleFluidVolume {
	private final long size;

	public FixedSizeFluidVolume(Identifier fluid, long amount, CompoundTag data, long size) {
		super(fluid, amount, data);
		this.size = size;
	}

	public FixedSizeFluidVolume(Identifier fluid, long amount, long size) {
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
	public FluidVolume add(FluidVolume volume, boolean simulate) {
		Identifier fluidA = volume.fluid();
		if (FluidIds.miscible(fluidA, this.fluid)) {
			long amount = Math.min(volume.amount(), this.size - this.amount);
			if (!simulate) {
				this.data = FluidPropertyMerger.INSTANCE.merge(this.fluid, this.data(), this.amount(), volume.data(), amount);
				this.amount += amount;
				this.fluid = volume.fluid();
			}
			if (amount == volume.amount()) { return ImmutableFluidVolume.EMPTY; } else return new SimpleFluidVolume(this.fluid, volume.amount() - amount, this.data.copy());
		}
		return volume;
	}
}
