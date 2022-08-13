package net.fabricmc.fabric.mixin.item;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface AbstractFurnaceBlockEntityAccessor {
	@Accessor
	DefaultedList<ItemStack> getInventory();
}
