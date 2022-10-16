package net.fabricmc.fabric.api.ingredient.v1;

import java.util.Objects;

import net.minecraft.recipe.Ingredient;

import net.fabricmc.fabric.impl.ingredient.builtin.OrIngredient;

/**
 * Custom ingredients directly provided by fabric.
 */
public final class DefaultCustomIngredients {
	public static Ingredient or(Ingredient... ingredients) {
		for (Ingredient ing : ingredients) Objects.requireNonNull(ing);

		return new OrIngredient(ingredients).toVanilla();
	}

	static {
		CustomIngredientSerializer.register(OrIngredient.Serializer.INSTANCE);
	}

	private DefaultCustomIngredients() {
	}
}
