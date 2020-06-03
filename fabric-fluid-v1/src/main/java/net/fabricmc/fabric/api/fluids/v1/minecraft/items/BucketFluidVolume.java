package net.fabricmc.fabric.api.fluids.v1.minecraft.items;

import static net.fabricmc.fabric.api.fluids.v1.math.Drops.getBucket;

import com.google.common.annotations.VisibleForTesting;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;

import net.fabricmc.fabric.Action;
import net.fabricmc.fabric.api.fluids.v1.container.volume.FluidVolume;
import net.fabricmc.fabric.api.fluids.v1.container.volume.ImmutableFluidVolume;
import net.fabricmc.fabric.api.fluids.v1.container.volume.SimpleFluidVolume;
import net.fabricmc.fabric.api.fluids.v1.item.ItemSink;
import net.fabricmc.fabric.api.fluids.v1.minecraft.FluidUtil;
import net.fabricmc.fabric.api.fluids.v1.properties.FluidPropertyMerger;
import net.fabricmc.fabric.impl.fluids.BucketItemAccessor;

public class BucketFluidVolume implements FluidVolume {
	@VisibleForTesting public final ItemStack stack;
	private final ItemSink output;

	public BucketFluidVolume(ItemStack stack, ItemSink output) {
		this.stack = stack;
		this.output = output;
	}

	@Override
	public FluidVolume drain(Fluid fluid, long amount, Action action) {
		Fluid thisFluid = this.fluid();
		if (!Fluids.EMPTY.equals(thisFluid) && FluidUtil.miscible(thisFluid, fluid)) {
			int items = Math.toIntExact(Math.min(this.stack.getCount(), amount / getBucket()));

			if (action.perform()) {
				this.stack.setCount(this.stack.getCount() - items);
				this.output.push(new ItemStack(Items.BUCKET, items), action);
			}

			CompoundTag toCopy = this.data();
			return new SimpleFluidVolume(thisFluid, items * getBucket(), toCopy == null ? null : toCopy.copy());
		}

		return ImmutableFluidVolume.EMPTY;
	}

	@Override
	public Fluid fluid() {
		return this.stack.isEmpty() ? Fluids.EMPTY : ((BucketItemAccessor) this.stack.getItem()).getFluid();
	}

	@Override
	public CompoundTag data() {
		return this.stack.getTag();
	}

	@Override
	public long amount() {
		return getBucket() * this.stack.getCount();
	}


	@Override
	public FluidVolume consume(FluidVolume container, Action action) {
		Fluid fluid = this.fluid();
		if (FluidUtil.miscible(fluid, container.fluid())) {
			int itemCapacity;
			if (fluid == Fluids.EMPTY) {
				itemCapacity = this.stack.getCount();
			} else {
				itemCapacity = this.stack.getMaxCount() - this.stack.getCount();
			}

			int count = (int) Math.min(itemCapacity, container.amount() / getBucket());
			FluidVolume toTake = container.drain(count * getBucket(), Action.SIMULATE);
			if (toTake.amount() % getBucket() == 0) {
				if (action.simulate()) {
					container = container.simpleCopy();
				}
				if (fluid == Fluids.EMPTY) {
					this.output.push(new ItemStack(container.fluid().getBucketItem(), (int) (toTake.amount() / getBucket())), action);
				} else {
					count = this.output.take(new ItemStack(Items.BUCKET, (int) (toTake.amount() / getBucket())), action).getCount();
				}
				container.drain(count * getBucket(), Action.PERFORM);
				if (action.perform()) {
					if(fluid != Fluids.EMPTY) {
						this.stack.setCount(this.stack.getCount() + count);
						this.stack.setTag(FluidPropertyMerger.INSTANCE.merge(this.fluid(), this.data(), this.amount(), toTake.data(), count * getBucket()));
					} else {
						this.stack.setCount(this.stack.getCount() - count);
					}
				}
			}
		}
		return container;
	}

	@Override
	public boolean isEmpty() {
		return this.stack.isEmpty() || this.fluid() == Fluids.EMPTY;
	}

	@Override
	public boolean isImmutable() {
		return this.isEmpty();
	}
}
