package net.fabricmc.fabric.api.fluids.v1.minecraft.items;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.Action;
import net.fabricmc.fabric.api.fluids.v1.item.ItemSink;
import net.fabricmc.fabric.api.fluids.v1.math.Drops;
import net.fabricmc.fabric.api.fluids.v1.minecraft.FluidIds;
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
	protected Identifier getFluid() {
		if (this.stack.isEmpty()) return FluidIds.EMPTY;
		if (this.stack.getItem() == Items.MILK_BUCKET) return FluidIds.MILK;

		Fluid fluid = ((BucketItemAccessor) this.stack.getItem()).getFluid();
		return FluidIds.getId(fluid);
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
	protected void addFilled(ItemSink sink, Identifier fluid, int items, Action action) {
		if (this.stack.getItem() == Items.BUCKET) {
			if (fluid.equals(FluidIds.MILK)) {
				sink.push(new ItemStack(Items.MILK_BUCKET, items), action);
			} else {
				sink.push(new ItemStack(FluidIds.forId(fluid).getBucketItem(), items), action);
			}

			return;
		}

		throw new IllegalStateException();
	}
}
