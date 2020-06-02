package net.fabricmc.fabric.api.fluids.v1.minecraft.entities;

import net.minecraft.entity.passive.CowEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.Action;
import net.fabricmc.fabric.api.fluids.v1.container.volume.FluidVolume;
import net.fabricmc.fabric.api.fluids.v1.container.volume.ImmutableFluidVolume;
import net.fabricmc.fabric.api.fluids.v1.container.volume.SimpleFluidVolume;
import net.fabricmc.fabric.api.fluids.v1.minecraft.FluidIds;

/**
 * this is never used, but it's an example on how to implement sided fluid containers.
 * on entities, you can use it if you want, in vanilla, cows are infinite reservoirs of
 * milk, as so long as they aren't babies
 */
public class CowFluidVolume implements FluidVolume {
	private final CowEntity cow;

	public CowFluidVolume(CowEntity cow) {
		this.cow = cow;
	}

	@Override
	public Identifier fluid() {
		return this.cow.isBaby() ? FluidIds.EMPTY : FluidIds.MILK;
	}

	@Override
	public CompoundTag data() {
		return new CompoundTag();
	}

	@Override
	public long amount() {
		// damn cows hold a ton of milk
		return this.cow.isBaby() ? 0 : Long.MAX_VALUE;
	}

	@Override
	public FluidVolume drain(Identifier fluid, long amount, Action action) {
		if (fluid == null || fluid.equals(FluidIds.MILK)) {
			return new SimpleFluidVolume(FluidIds.MILK, amount);
		}

		return ImmutableFluidVolume.EMPTY;
	}

	@Override
	public FluidVolume add(FluidVolume container, Action action) {
		return container;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean isImmutable() {
		return true;
	}
}
