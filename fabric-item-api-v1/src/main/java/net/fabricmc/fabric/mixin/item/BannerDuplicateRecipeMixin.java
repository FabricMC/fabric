package net.fabricmc.fabric.mixin.item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.BannerDuplicateRecipe;
import net.minecraft.util.collection.DefaultedList;

@Mixin(BannerDuplicateRecipe.class)
public class BannerDuplicateRecipeMixin {

	/**
	 * @author FabricMC
	 * @reason support stack aware recipe remainders
	 */
	@Overwrite
	public DefaultedList<ItemStack> getRemainder(CraftingInventory craftingInventory) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(craftingInventory.size(), ItemStack.EMPTY);

		for(int i = 0; i < defaultedList.size(); ++i) {
			ItemStack itemStack = craftingInventory.getStack(i);
			if (!itemStack.isEmpty()) {
				if (itemStack.getItem().hasRecipeRemainder(itemStack)) {
					defaultedList.set(i, itemStack.getItem().getRecipeRemainder(itemStack));
				} else if (itemStack.hasNbt() && BannerBlockEntity.getPatternCount(itemStack) > 0) {
					ItemStack itemStack2 = itemStack.copy();
					itemStack2.setCount(1);
					defaultedList.set(i, itemStack2);
				}
			}
		}

		return defaultedList;
	}
}
