package net.fabricmc.fabric.mixin.fluids.items;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.access.fluids.BucketItemAccess;
import net.fabricmc.fabric.api.fluids.v1.container.FluidContainer;
import net.fabricmc.fabric.api.fluids.v1.item.ItemFluidContainer;
import net.fabricmc.fabric.api.fluids.v1.item.ItemSink;
import net.fabricmc.fabric.api.fluids.v1.minecraft.items.BucketItemFluidContainer;

@Mixin (BucketItem.class)
public class BucketItemMixin implements BucketItemAccess, ItemFluidContainer {
	@Shadow @Final private Fluid fluid;

	@Override
	public Fluid getFluid() {
		return this.fluid;
	}

	@Override
	public FluidContainer getContainer(ItemSink waste, ItemStack stack) {
		return new BucketItemFluidContainer(stack, waste);
	}
}
