package net.fabricmc.fabric.api.fluids.minecraft.items;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.fluids.items.ItemSink;

public class FishBucketItemFluidContainer extends BucketItemFluidContainer {
	public FishBucketItemFluidContainer(ItemStack stack, ItemSink output) {
		super(stack, output);
	}

	@Override
	protected void addFilled(ItemSink sink, Identifier fluid, int items, boolean simulate) {
		super.addFilled(sink, fluid, items, simulate);
		// todo support fish buckets
	}
}
