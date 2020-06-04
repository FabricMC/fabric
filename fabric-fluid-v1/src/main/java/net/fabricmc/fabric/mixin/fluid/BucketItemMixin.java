package net.fabricmc.fabric.mixin.fluid;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.fluid.v1.container.FluidContainer;
import net.fabricmc.fabric.api.fluid.v1.item.ItemFluidContainer;
import net.fabricmc.fabric.api.fluid.v1.item.ItemSink;
import net.fabricmc.fabric.api.fluid.v1.minecraft.items.BucketFluidVolume;
import net.fabricmc.fabric.impl.fluid.BucketItemAccessor;

@Mixin (BucketItem.class)
public class BucketItemMixin implements BucketItemAccessor, ItemFluidContainer {
	@Shadow
	@Final
	private Fluid fluid;

	@Override
	public Fluid getFluid() {
		return this.fluid;
	}

	@Override
	public FluidContainer getContainer(ItemSink waste, ItemStack stack) {
		return new BucketFluidVolume(stack, waste);
	}
}
