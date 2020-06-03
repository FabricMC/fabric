package net.fabricmc.fabric.api.fluids.v1.minecraft.items;

import com.google.common.annotations.VisibleForTesting;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import net.fabricmc.fabric.Action;
import net.fabricmc.fabric.api.fluids.v1.container.volume.FluidVolume;
import net.fabricmc.fabric.api.fluids.v1.container.volume.ImmutableFluidVolume;
import net.fabricmc.fabric.api.fluids.v1.container.volume.SimpleFluidVolume;
import net.fabricmc.fabric.api.fluids.v1.item.ItemSink;
import net.fabricmc.fabric.api.fluids.v1.math.Drops;
import net.fabricmc.fabric.api.fluids.v1.minecraft.FluidUtil;
import net.fabricmc.fabric.api.fluids.v1.properties.FluidPropertyMerger;

public abstract class UnitItemFluidContainer implements FluidVolume {
	@VisibleForTesting public final ItemStack stack;
	private final ItemSink output;

	public UnitItemFluidContainer(ItemStack stack, ItemSink output) {
		this.stack = stack;
		this.output = output;
	}

	@Override
	public FluidVolume drain(Fluid fluid, long amount, Action action) {
		if (!Fluids.EMPTY.equals(this.fluid())) {
			int items = Math.toIntExact(Math.min(this.stack.getCount(), amount / this.unit()));

			if (action.perform()) {
				this.stack.setCount(this.stack.getCount() - items);
			}

			CompoundTag toCopy = this.data();
			return new SimpleFluidVolume(this.getFluid(), items * this.unit(), toCopy == null ? null : toCopy.copy());
		}

		return ImmutableFluidVolume.EMPTY;
	}

	@Override
	public Fluid fluid() {
		return this.stack.isEmpty() ? Fluids.EMPTY : this.getFluid();
	}

	@Override
	public CompoundTag data() {
		return this.stack.getTag();
	}

	@Override
	public long amount() {
		return this.unit() * this.stack.getCount();
	}

	protected abstract long unit();

	protected abstract Fluid getFluid();

	@Override
	public FluidVolume add(FluidVolume container, Action simulate) {
		if (this.stack.isEmpty()) {
			return container;
		}

		long original = container.amount();

		if (FluidUtil.miscible(container.fluid(), this.fluid())) {
			Fluid fluid = FluidUtil.tryFindNonEmpty(this.fluid(), container.fluid());
			long toAdd = Math.min((this.stack.getMaxCount() - this.stack.getCount()) * this.unit(), Drops.floor(container.amount(), this.unit()));

			if (!this.empty()) {
				ItemStack consume = this.output.take(new ItemStack(this.consumeOnAdd(), (int) (toAdd / this.unit())), Action.SIMULATE);
				toAdd = Math.min(toAdd, consume.getCount() * this.unit());
				this.output.take(consume, simulate);
			} else {
				this.addFilled(this.output, fluid, (int) (toAdd / this.unit()), simulate);
			}

			if (toAdd == 0) {
				return container;
			}

			if (simulate.perform()) {
				this.stack.setCount((int) (this.stack.getCount() + (toAdd / this.unit())));
				this.stack.setTag(FluidPropertyMerger.INSTANCE.merge(fluid, this.data(), this.stack.getCount() * this.unit(), container.data(), container.amount()));
			}

			return new SimpleFluidVolume(container.fluid(), original - toAdd, container.data());
		}

		return ImmutableFluidVolume.EMPTY;
	}

	// true if empty bucket, bottle, or other
	protected abstract boolean empty();

	protected abstract Item consumeOnAdd();

	// byproduct of filling (water buckets, water bottles etc.)
	protected abstract void addFilled(ItemSink sink, Fluid fluid, int items, Action action);

	@Override
	public boolean isEmpty() {
		return this.stack.isEmpty();
	}

	@Override
	public boolean isImmutable() {
		return this.isEmpty();
	}
}
