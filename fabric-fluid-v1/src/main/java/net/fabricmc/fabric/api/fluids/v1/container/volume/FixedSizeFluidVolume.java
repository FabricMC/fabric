package net.fabricmc.fabric.api.fluids.v1.container.volume;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.Action;
import net.fabricmc.fabric.api.fluids.v1.minecraft.FluidIds;
import net.fabricmc.fabric.api.fluids.v1.properties.FluidPropertyMerger;

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
	public FluidVolume add(FluidVolume volume, Action action) {
		Identifier fluidA = volume.fluid();

		if (FluidIds.miscible(fluidA, this.fluid)) {
			long amount = Math.min(volume.amount(), this.size - this.amount);

			if (action.perform()) {
				this.data = FluidPropertyMerger.INSTANCE.merge(this.fluid, this.data(), this.amount(), volume.data(), amount);
				this.amount += amount;
				this.fluid = volume.fluid();
			}

			if (amount == volume.amount()) {
				return ImmutableFluidVolume.EMPTY;
			} else {
				return new SimpleFluidVolume(this.fluid, volume.amount() - amount, this.data.copy());
			}
		}

		return volume;
	}
}
