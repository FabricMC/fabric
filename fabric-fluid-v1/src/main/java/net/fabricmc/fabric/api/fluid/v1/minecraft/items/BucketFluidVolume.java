package net.fabricmc.fabric.api.fluid.v1.minecraft.items;

import static net.fabricmc.fabric.api.fluid.v1.math.Drops.getBucket;

import com.google.common.annotations.VisibleForTesting;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;

import net.fabricmc.fabric.api.fluid.v1.Action;
import net.fabricmc.fabric.api.fluid.v1.FluidView;
import net.fabricmc.fabric.api.fluid.v1.container.volume.FluidVolume;
import net.fabricmc.fabric.api.fluid.v1.container.volume.ImmutableFluidVolume;
import net.fabricmc.fabric.api.fluid.v1.container.volume.SimpleFluidVolume;
import net.fabricmc.fabric.api.fluid.v1.item.ItemSink;
import net.fabricmc.fabric.api.fluid.v1.properties.FluidPropertyMerger;
import net.fabricmc.fabric.impl.fluid.BucketItemAccessor;

public class BucketFluidVolume implements FluidVolume {
	@VisibleForTesting public final ItemStack stack;
	private final ItemSink output;

	public BucketFluidVolume(ItemStack stack, ItemSink output) {
		this.stack = stack;
		this.output = output;
	}

	@Override
	public FluidVolume drain(Fluid fluid, long amount, Action action) {
		Fluid thisFluid = this.getFluid();

		if (!Fluids.EMPTY.equals(thisFluid) && FluidView.mixable(thisFluid, fluid)) {
			int items = Math.toIntExact(Math.min(this.stack.getCount(), amount / getBucket()));

			if (action.shouldPerform()) {
				this.stack.setCount(this.stack.getCount() - items);
				this.output.push(new ItemStack(Items.BUCKET, items), action);
			}

			CompoundTag toCopy = this.getData();
			return new SimpleFluidVolume(thisFluid, items * getBucket(), toCopy == null ? null : toCopy.copy());
		}

		return ImmutableFluidVolume.EMPTY;
	}

	@Override
	public FluidVolume consume(FluidVolume container, Action action) {
		Fluid fluid = this.getFluid();

		if (FluidView.mixable(fluid, container.getFluid())) {
			int itemCapacity;

			if (fluid == Fluids.EMPTY) {
				itemCapacity = this.stack.getCount();
			} else {
				itemCapacity = this.stack.getMaxCount() - this.stack.getCount();
			}

			int count = (int) Math.min(itemCapacity, container.getAmount() / getBucket());
			FluidVolume toTake = container.drain(count * getBucket(), Action.SIMULATE);

			if (toTake.getAmount() % getBucket() == 0) {
				if (action.isSimulation()) {
					container = container.simpleCopy();
				}

				if (fluid == Fluids.EMPTY) {
					this.output.push(new ItemStack(container.getFluid().getBucketItem(), (int) (toTake.getAmount() / getBucket())), action);
				} else {
					count = this.output.take(new ItemStack(Items.BUCKET, (int) (toTake.getAmount() / getBucket())), action).getCount();
				}

				container.drain(count * getBucket(), Action.PERFORM);

				if (action.shouldPerform()) {
					if (fluid != Fluids.EMPTY) {
						this.stack.setCount(this.stack.getCount() + count);
						this.stack.setTag(FluidPropertyMerger.INSTANCE.merge(this.getFluid(), this.getData(), this.getAmount(), toTake.getData(), count * getBucket()));
					} else {
						this.stack.setCount(this.stack.getCount() - count);
					}
				}
			}
		}

		return container;
	}

	@Override
	public long getAmount() {
		return getBucket() * this.stack.getCount();
	}

	@Override
	public Fluid getFluid() {
		return this.stack.isEmpty() ? Fluids.EMPTY : ((BucketItemAccessor) this.stack.getItem()).getFluid();
	}

	@Override
	public CompoundTag getData() {
		return this.stack.getTag();
	}

	@Override
	public boolean isEmpty() {
		return this.stack.isEmpty() || this.getFluid() == Fluids.EMPTY;
	}

	@Override
	public boolean isImmutable() {
		return this.isEmpty();
	}
}
