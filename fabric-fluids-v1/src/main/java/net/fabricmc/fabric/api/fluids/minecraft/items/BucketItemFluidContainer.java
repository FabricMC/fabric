package net.fabricmc.fabric.api.fluids.minecraft.items;

import java.lang.reflect.Field;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.access.fluids.BucketItemAccess;
import net.fabricmc.fabric.api.fluids.items.ItemSink;
import net.fabricmc.fabric.api.fluids.math.Drops;
import net.fabricmc.fabric.api.fluids.minecraft.FluidIds;

public class BucketItemFluidContainer extends UnitItemFluidContainer {

	public BucketItemFluidContainer(ItemStack stack, ItemSink output) {
		super(stack, output);
	}

	@Override
	protected Identifier getFluid() {
		if (this.stack.isEmpty()) return FluidIds.EMPTY;
		if (this.stack.getItem() == Items.MILK_BUCKET) return FluidIds.MILK;

		Fluid fluid;
		if (test) { // todo remove hack
			try {
				Field field = BucketItem.class.getDeclaredField("fluid");
				field.setAccessible(true);
				fluid = (Fluid) field.get(this.stack.getItem());
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
		} else {
			fluid = ((BucketItemAccess) this.stack.getItem()).getFluid();
		}
		return FluidIds.getId(fluid);
	}

	@Override
	protected long unit() {
		return Drops.getBucket();
	}

	@Override
	protected Item consumeOnAdd() {
		return Items.BUCKET;
	}

	@Override
	protected boolean empty() {
		return this.stack.getItem() == Items.BUCKET;
	}

	@Override
	protected void addFilled(ItemSink sink, Identifier fluid, int items, boolean simulate) {
		if (this.stack.getItem() == Items.BUCKET) {
			if (fluid.equals(FluidIds.MILK)) {
				sink.push(new ItemStack(Items.MILK_BUCKET, items), simulate);
			} else {
				sink.push(new ItemStack(FluidIds.forId(fluid).getBucketItem(), items), simulate);
			}
			return;
		}
		throw new IllegalStateException();
	}
}
