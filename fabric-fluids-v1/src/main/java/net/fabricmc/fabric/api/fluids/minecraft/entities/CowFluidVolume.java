package net.fabricmc.fabric.api.fluids.minecraft.entities;

import net.minecraft.entity.passive.CowEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.fluids.containers.volume.FluidVolume;
import net.fabricmc.fabric.api.fluids.containers.volume.ImmutableFluidVolume;
import net.fabricmc.fabric.api.fluids.containers.volume.SimpleFluidVolume;
import net.fabricmc.fabric.api.fluids.minecraft.FluidIds;

/**
 * this is never used, but it's an example on how to implement sided fluid containers
 * on entities, you can use it if you want, in vanilla, cows are infinite reservoirs of
 * milk, as so long as they aren't babies
 */
public class CowFluidVolume implements FluidVolume {
	// mixin to cow and implement the sided fluid container interface, bonus points if you can only milk a cow from under
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
	public FluidVolume drain(Identifier fluid, long amount, boolean simulate) {
		if (fluid == null || fluid.equals(FluidIds.MILK)) {
			return new SimpleFluidVolume(FluidIds.MILK, amount);
		}
		return ImmutableFluidVolume.EMPTY;
	}

	@Override
	public FluidVolume add(FluidVolume container, boolean simulate) {
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
