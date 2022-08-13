package net.fabricmc.fabric.mixin.item;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.recipe.BookCloningRecipe;

import net.minecraft.util.collection.DefaultedList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BookCloningRecipe.class)
public class BookCloningRecipeMixin {
	/**
	 * @author FabricMC
	 * @reason support stack aware recipe remainders
	 */
	@Overwrite
	public DefaultedList<ItemStack> getRemainder(CraftingInventory craftingInventory) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(craftingInventory.size(), ItemStack.EMPTY);

		for(int i = 0; i < defaultedList.size(); ++i) {
			ItemStack itemStack = craftingInventory.getStack(i);
			if (itemStack.getItem().hasRecipeRemainder(itemStack)) {
				defaultedList.set(i, itemStack.getItem().getRecipeRemainder(itemStack));
			} else if (itemStack.getItem() instanceof WrittenBookItem) {
				ItemStack itemStack2 = itemStack.copy();
				itemStack2.setCount(1);
				defaultedList.set(i, itemStack2);
				break;
			}
		}

		return defaultedList;
	}
}
