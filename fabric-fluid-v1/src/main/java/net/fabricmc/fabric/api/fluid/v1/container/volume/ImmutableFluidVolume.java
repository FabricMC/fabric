package net.fabricmc.fabric.api.fluid.v1.container.volume;

import java.util.Objects;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;

import net.fabricmc.fabric.api.fluid.v1.Action;

public class ImmutableFluidVolume implements FluidVolume {
	public static final ImmutableFluidVolume EMPTY = new ImmutableFluidVolume();

	private final Fluid fluid;
	private final long amount;
	private final CompoundTag tag;

	public ImmutableFluidVolume() {
		this(Fluids.EMPTY, 0);
	}

	public ImmutableFluidVolume(Fluid fluid, long amount) {
		this(fluid, amount, new CompoundTag());
	}

	public ImmutableFluidVolume(Fluid fluid, long amount, CompoundTag tag) {
		this.fluid = fluid;
		this.amount = amount;
		this.tag = tag;
	}

	@Override
	public FluidVolume drain(Fluid fluid, long amount, Action action) {
		return EMPTY;
	}

	@Override
	public FluidVolume consume(FluidVolume container, Action action) {
		return container;
	}

	@Override
	public boolean isEmpty() {
		return this.amount == 0 || Fluids.EMPTY.equals(this.fluid);
	}

	@Override
	public boolean isImmutable() {
		return true;
	}

	@Override
	public int hashCode() {
		int result = this.getFluid() != null ? this.getFluid().hashCode() : 0;
		result = 31 * result + (int) (this.getAmount() ^ (this.getAmount() >>> 32));
		result = 31 * result + (this.getData() != null ? this.getData().hashCode() : 0);
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof FluidVolume)) return false;

		FluidVolume that = (FluidVolume) o;

		if (this.getAmount() != that.getAmount()) return false;
		if (!Objects.equals(this.getFluid(), that.getFluid())) return false;
		return Objects.equals(this.getData(), that.getData());
	}

	@Override
	public String toString() {
		return "ImmutableFluidVolume{" + "fluid=" + this.fluid + ", amount=" + this.amount + ", tag=" + this.tag + '}';
	}

	@Override
	public long getAmount() {
		return this.amount;
	}

	@Override
	public Fluid getFluid() {
		return this.fluid;
	}

	@Override
	public CompoundTag getData() {
		return this.tag == null ? null : this.tag.copy();
	}
}
