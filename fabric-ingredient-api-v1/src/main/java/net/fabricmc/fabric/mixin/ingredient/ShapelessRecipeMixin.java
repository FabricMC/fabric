package net.fabricmc.fabric.mixin.ingredient;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import net.fabricmc.fabric.impl.ingredient.ShapelessMatch;

@Mixin(ShapelessRecipe.class)
public class ShapelessRecipeMixin {
	@Final
	@Shadow
	DefaultedList<Ingredient> input;

	@Inject(
			at = @At(
					value = "INVOKE",
					target = "net/minecraft/recipe/RecipeMatcher.match(Lnet/minecraft/recipe/Recipe;Lit/unimi/dsi/fastutil/ints/IntList;)Z"
			),
			method = "matches",
			cancellable = true
	)
	public void customIngredientMatch(CraftingInventory craftingInventory, World world, CallbackInfoReturnable<Boolean> cir) {
		for (Ingredient ingredient : input) {
			if (ingredient.requiresTesting()) {
				cir.setReturnValue(ShapelessMatch.isMatch(craftingInventory, input));
				return;
			}
		}
	}
}
