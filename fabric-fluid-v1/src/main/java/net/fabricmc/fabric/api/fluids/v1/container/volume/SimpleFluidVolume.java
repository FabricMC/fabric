package net.fabricmc.fabric.api.fluids.v1.container.volume;

import java.util.Objects;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.Action;
import net.fabricmc.fabric.api.fluids.v1.math.Drops;
import net.fabricmc.fabric.api.fluids.v1.minecraft.FluidUtil;
import net.fabricmc.fabric.api.fluids.v1.properties.FluidPropertyMerger;

public class SimpleFluidVolume implements FluidVolume {
	protected Fluid fluid;
	protected long amount;
	protected CompoundTag data;

	public SimpleFluidVolume(Fluid fluid, long amount) {
		this(fluid, amount, new CompoundTag());
	}

	public SimpleFluidVolume(Fluid fluid, long amount, CompoundTag data) {
		if (Fluids.EMPTY.equals(fluid) || amount <= 0) {
			amount = 0;
			fluid = Fluids.EMPTY;
			data = (data == null || data.isEmpty()) ? data : new CompoundTag();
		}
		this.fluid = fluid;
		this.amount = amount;
		this.data = data;
	}

	public SimpleFluidVolume() {
		this(null, 0, new CompoundTag());
	}

	public SimpleFluidVolume(CompoundTag tag) {
		this.fluid = Registry.FLUID.get(new Identifier(tag.getString("fluid")));
		this.amount = Drops.fromTag(tag);
		this.data = tag.getCompound("data");
	}

	public CompoundTag toTag(CompoundTag tag) {
		tag.putString("fluid", this.fluid.toString());
		Drops.toTag(tag, this.amount);
		tag.put("data", this.data);
		return tag;
	}

	@Override
	public FluidVolume drain(Fluid fluid, long amount, Action action) {
		if (FluidUtil.miscible(this.fluid, fluid)) {
			amount = Drops.floor(Math.min(amount, this.amount), 1);
			fluid = this.fluid;

			if (action.perform()) {
				this.amount -= amount;
				this.updateEmpty();
				this.update();
			}

			return new SimpleFluidVolume(fluid, amount, this.data.copy());
		}

		return ImmutableFluidVolume.EMPTY;
	}

	@Override
	public FluidVolume consume(FluidVolume volume, Action action) {
		if (FluidUtil.miscible(volume.fluid(), this.fluid)) {
			if(action.simulate()) {
				volume = volume.simpleCopy();
			}

			// drain as much as we can
			Fluid drained = volume.fluid();
			long amount = volume.drain(Long.MAX_VALUE, Action.PERFORM).amount();
			if (action.perform() && amount != 0) {
				this.data = FluidPropertyMerger.INSTANCE.merge(this.fluid, this.data(), this.amount(), volume.data(), amount);
				this.amount += amount;
				this.fluid = FluidUtil.tryFindNonEmpty(drained, this.fluid);
				this.updateEmpty();
			}
		}
		return volume;
	}

	@Override
	public boolean isEmpty() {
		return this.fluid == Fluids.EMPTY;
	}

	@Override
	public boolean isImmutable() {
		return false;
	}

	private void updateEmpty() {
		if (this.amount <= 0) {
			this.fluid = Fluids.EMPTY;
			this.data = new CompoundTag();
			this.amount = 0;
		}
	}

	protected void update() {
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
		return "SimpleFluidVolume{" + "fluid=" + this.fluid + ", amount=" + this.amount + ", data=" + this.data + '}';
	}

	@Override
	public Fluid fluid() {
		return this.fluid;
	}

	@Override
	public CompoundTag data() {
		return this.data;
	}

	@Override
	public long amount() {
		return this.amount;
	}
}
