package net.fabricmc.fabric.mixin.item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.collection.DefaultedList;

@Mixin(Recipe.class)
public interface RecipeMixin<C extends Inventory> {

	/**
	 * @author FabricMC
	 * @reason support stack aware recipe remainders
	 */
	@Overwrite
	default DefaultedList<ItemStack> getRemainder(C inventory) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);

		for (int i = 0; i < defaultedList.size(); ++i) {
			ItemStack item = inventory.getStack(i);
			if (item.getItem().hasRecipeRemainder(item)) {
				defaultedList.set(i, item.getItem().getRecipeRemainder(item));
			}
		}

		return defaultedList;
	}
}
