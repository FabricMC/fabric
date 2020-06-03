package net.fabricmc.fabric.test.fluid.items;

import java.util.Iterator;

import com.google.common.collect.Iterators;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import net.fabricmc.fabric.Action;
import net.fabricmc.fabric.api.fluids.v1.container.FluidContainer;
import net.fabricmc.fabric.api.fluids.v1.container.volume.FluidVolume;
import net.fabricmc.fabric.api.fluids.v1.container.volume.ImmutableFluidVolume;
import net.fabricmc.fabric.api.fluids.v1.container.volume.SimpleFluidVolume;
import net.fabricmc.fabric.api.fluids.v1.item.ItemFluidContainer;
import net.fabricmc.fabric.api.fluids.v1.item.ItemSink;
import net.fabricmc.fabric.api.fluids.v1.math.Drops;
import net.fabricmc.fabric.api.fluids.v1.minecraft.FluidUtil;
import net.fabricmc.fabric.api.fluids.v1.properties.FluidPropertyMerger;

public class FluidShard extends Item implements ItemFluidContainer {
	private static final long ONE_THIRD = Drops.fraction(1, 3);

	public FluidShard(Settings settings) {
		super(settings);
	}

	@Override
	public FluidContainer getContainer(ItemSink waste, ItemStack stack) {
		return new FluidVolume() {
			@Override
			public FluidVolume drain(Fluid fluid, long amount, Action action) {
				if(FluidUtil.miscible(fluid, Fluids.WATER)) {
					int count = (int) Math.min(stack.getCount(), Math.min(amount / ONE_THIRD, Integer.MAX_VALUE));
					if (action.perform()) {
						stack.setCount(stack.getCount() - count);
					}
					return new SimpleFluidVolume(Fluids.WATER, count * ONE_THIRD, stack.getTag());
				}
				return ImmutableFluidVolume.EMPTY;
			}

			@Override
			public FluidVolume consume(FluidVolume container, Action action) {
				if(container.fluid() == Fluids.WATER) {
					int count = (int) Math.min(stack.getMaxCount() - stack.getCount(), container.amount() / ONE_THIRD);
					FluidVolume toTake = container.drain(count * ONE_THIRD, Action.SIMULATE);

					if (toTake.amount() % ONE_THIRD == 0) {
						if(action.simulate()) {
							container = container.simpleCopy();
						}

						count = (int) (toTake.amount() / ONE_THIRD);
						container.drain(count * ONE_THIRD, Action.PERFORM);

						if(action.perform()) {
							stack.setCount(stack.getCount() + count);
							stack.setTag(FluidPropertyMerger.INSTANCE.merge(this.fluid(), this.data(), this.amount(), toTake.data(), toTake.amount()));
						}
					}
				}

				return container;
			}

			@Override
			public Fluid fluid() {
				return Fluids.WATER;
			}

			@Override
			public CompoundTag data() {
				return stack.getTag();
			}

			@Override
			public Iterator<FluidVolume> iterator() {
				return Iterators.singletonIterator(this);
			}

			@Override
			public long amount() {
				return stack.getCount() * ONE_THIRD;
			}

			@Override
			public boolean isEmpty() {
				return stack.isEmpty();
			}

			@Override
			public boolean isImmutable() {
				return false;
			}
		};
	}
}
