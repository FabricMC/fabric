package net.fabricmc.fabric.api.brewing;

import net.fabricmc.fabric.impl.brewing.BrewingRecipesImpl;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public interface BrewingRecipes {
	BrewingRecipes INSTANCE = new BrewingRecipesImpl();
	List<BrewingRecipe> getRelevantRecipes(World world, ItemStack stack, boolean checkingIngredient);
}
