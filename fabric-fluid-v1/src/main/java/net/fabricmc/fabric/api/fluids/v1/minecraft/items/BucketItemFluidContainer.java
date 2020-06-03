package net.fabricmc.fabric.api.fluids.v1.minecraft.items;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import net.fabricmc.fabric.Action;
import net.fabricmc.fabric.api.fluids.v1.item.ItemSink;
import net.fabricmc.fabric.api.fluids.v1.math.Drops;
import net.fabricmc.fabric.impl.fluids.BucketItemAccessor;

public class BucketItemFluidContainer extends UnitItemFluidContainer {
	public BucketItemFluidContainer(ItemStack stack, ItemSink output) {
		super(stack, output);
	}

	@Override
	protected long unit() {
		return Drops.getBucket();
	}

	@Override
	protected Fluid getFluid() {
		if (this.stack.isEmpty()) return Fluids.EMPTY;
		Fluid fluid = ((BucketItemAccessor) this.stack.getItem()).getFluid();
		return fluid;
	}

	@Override
	protected boolean empty() {
		return this.stack.getItem() == Items.BUCKET;
	}

	@Override
	protected Item consumeOnAdd() {
		return Items.BUCKET;
	}

	@Override
	protected void addFilled(ItemSink sink, Fluid fluid, int items, Action action) {
		if (this.stack.getItem() == Items.BUCKET) {
			sink.push(new ItemStack(fluid.getBucketItem(), items), action);
			return;
		}

		throw new IllegalStateException();
	}
}
