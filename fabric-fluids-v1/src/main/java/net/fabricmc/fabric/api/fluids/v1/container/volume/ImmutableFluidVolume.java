package net.fabricmc.fabric.api.fluids.v1.container.volume;

import java.util.Objects;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.fluids.v1.minecraft.FluidIds;

public class ImmutableFluidVolume implements FluidVolume {
	public static final ImmutableFluidVolume EMPTY = new ImmutableFluidVolume();

	private final Identifier fluid;
	private final long amount;
	private final CompoundTag tag;

	public ImmutableFluidVolume() {
		this(FluidIds.EMPTY, 0);
	}

	public ImmutableFluidVolume(Identifier fluid, long amount) {
		this(fluid, amount, new CompoundTag());
	}

	public ImmutableFluidVolume(Identifier fluid, long amount, CompoundTag tag) {
		this.fluid = fluid;
		this.amount = amount;
		this.tag = tag;
	}

	@Override
	public FluidVolume drain(Identifier fluid, long amount, boolean simulate) {
		return EMPTY;
	}

	@Override
	public FluidVolume add(FluidVolume container, boolean simulate) {
		return EMPTY;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean isImmutable() {
		return true;
	}

	@Override
	public int hashCode() {
		int result = this.fluid() != null ? this.fluid().hashCode() : 0;
		result = 31 * result + (int) (this.amount() ^ (this.amount() >>> 32));
		result = 31 * result + (this.data() != null ? this.data().hashCode() : 0);
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof FluidVolume)) return false;

		FluidVolume that = (FluidVolume) o;

		if (this.amount() != that.amount()) return false;
		if (!Objects.equals(this.fluid(), that.fluid())) return false;
		return Objects.equals(this.data(), that.data());
	}

	@Override
	public String toString() {
		return "ImmutableFluidVolume{" + "fluid=" + this.fluid + ", amount=" + this.amount + ", tag=" + this.tag + '}';
	}

	@Override
	public Identifier fluid() {
		return this.fluid;
	}

	@Override
	public CompoundTag data() {
		return this.tag.copy();
	}

	@Override
	public long amount() {
		return this.amount;
	}
}
