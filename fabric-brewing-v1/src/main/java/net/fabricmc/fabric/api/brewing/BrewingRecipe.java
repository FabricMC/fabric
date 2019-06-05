package net.fabricmc.fabric.api.brewing;

import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;

import java.util.List;

public interface BrewingRecipe extends Recipe<Inventory> {
	PotionIngredient getInput();
	PotionIngredient getBasePotion();
	PotionIngredient getOutputPotion();
	List<Integer> getMatchedSlots();
}
