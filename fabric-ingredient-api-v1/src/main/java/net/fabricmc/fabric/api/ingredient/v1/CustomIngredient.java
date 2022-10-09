package net.fabricmc.fabric.api.ingredient.v1;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import net.fabricmc.fabric.impl.ingredient.CustomIngredientImpl;

// TODO: what about getMatchingItemIds ?
public interface CustomIngredient {
	boolean matchesStack(ItemStack stack);
	// TODO: match yarn name? this name derived from old 1.16 yarn Recipe#getPreviewInputs() is more accurate
	ItemStack[] getPreviewStacks();
	boolean ignoresNbt();

	CustomIngredientSerializer<?> getSerializer();

	default Ingredient toVanilla() {
		return new CustomIngredientImpl(this);
	}
}
