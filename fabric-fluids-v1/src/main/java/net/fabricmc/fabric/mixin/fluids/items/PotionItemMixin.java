package net.fabricmc.fabric.mixin.fluids.items;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;

import net.fabricmc.fabric.api.fluids.containers.FluidContainer;
import net.fabricmc.fabric.api.fluids.items.ItemFluidContainer;
import net.fabricmc.fabric.api.fluids.items.ItemSink;
import net.fabricmc.fabric.api.fluids.minecraft.items.BottleItemFluidContainer;

@Mixin (PotionItem.class)
public class PotionItemMixin implements ItemFluidContainer {

	@Override
	public FluidContainer getContainer(ItemSink waste, ItemStack stack) {
		return new BottleItemFluidContainer(stack, waste);
	}
}
