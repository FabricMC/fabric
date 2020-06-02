package net.fabricmc.fabric.api.fluids.v1.minecraft.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.fluids.v1.item.ItemSink;
import net.fabricmc.fabric.api.fluids.v1.math.Drops;
import net.fabricmc.fabric.api.fluids.v1.minecraft.FluidIds;

public class BowlItemFluidContainer extends UnitItemFluidContainer {
	public BowlItemFluidContainer(ItemStack stack, ItemSink output) {
		super(stack, output);
	}

	@Override
	protected long unit() {
		return Drops.fraction(1, 3);
	}

	@Override
	protected Identifier getFluid() {
		Item item = this.stack.getItem();
		if (item == Items.MUSHROOM_STEW) return FluidIds.MUSHROOM_STEW;
		if (item == Items.SUSPICIOUS_STEW) return FluidIds.SUSPICIOUS_STEW;
		if (item == Items.BOWL) return FluidIds.EMPTY;
		throw new IllegalArgumentException("bad item");
	}

	@Override
	protected boolean empty() {
		return this.stack.getItem() == Items.BOWL;
	}

	@Override
	protected Item consumeOnAdd() {
		return Items.BOWL;
	}

	@Override
	protected void addFilled(ItemSink sink, Identifier fluid, int items, boolean simulate) {
		if (fluid == FluidIds.MUSHROOM_STEW) sink.push(new ItemStack(Items.MUSHROOM_STEW, items), simulate);
		if (fluid == FluidIds.SUSPICIOUS_STEW) sink.push(new ItemStack(Items.SUSPICIOUS_STEW, items), simulate);
		if (fluid == FluidIds.EMPTY) sink.push(new ItemStack(Items.BOWL, items), simulate);
	}
}
