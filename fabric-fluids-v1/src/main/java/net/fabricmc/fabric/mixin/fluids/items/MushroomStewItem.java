package net.fabricmc.fabric.mixin.fluids.items;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.fluids.v1.container.FluidContainer;
import net.fabricmc.fabric.api.fluids.v1.item.ItemFluidContainer;
import net.fabricmc.fabric.api.fluids.v1.item.ItemSink;
import net.fabricmc.fabric.api.fluids.v1.minecraft.items.BowlItemFluidContainer;

@Mixin (MushroomStewItem.class)
public class MushroomStewItem implements ItemFluidContainer {
	@Override
	public FluidContainer getContainer(ItemSink waste, ItemStack stack) {
		return new BowlItemFluidContainer(stack, waste);
	}
}
