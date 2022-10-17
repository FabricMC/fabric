package net.fabricmc.fabric.mixin.ingredient;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import net.fabricmc.fabric.impl.ingredient.ShapelessMatch;

@Mixin(ShapelessRecipe.class)
public class ShapelessRecipeMixin {
	@Final
	@Shadow
	DefaultedList<Ingredient> input;
	@Unique
	private boolean fabric_requiresTesting = false;

	@Inject(at = @At("RETURN"), method = "<init>")
	private void cacheRequiresTesting(Identifier id, String group, ItemStack output, DefaultedList<Ingredient> input, CallbackInfo ci) {
		for (Ingredient ingredient : input) {
			if (ingredient.requiresTesting()) {
				fabric_requiresTesting = true;
				break;
			}
		}
	}

	@Inject(at = @At("HEAD"), method = "matches", cancellable = true)
	public void customIngredientMatch(CraftingInventory craftingInventory, World world, CallbackInfoReturnable<Boolean> cir) {
		if (fabric_requiresTesting) {
			List<ItemStack> nonEmptyStacks = new ArrayList<>(craftingInventory.size());

			for (int i = 0; i < craftingInventory.size(); ++i) {
				ItemStack stack = craftingInventory.getStack(i);

				if (!stack.isEmpty()) {
					nonEmptyStacks.add(stack);
				}
			}

			cir.setReturnValue(ShapelessMatch.isMatch(nonEmptyStacks, input));
		}
	}
}
