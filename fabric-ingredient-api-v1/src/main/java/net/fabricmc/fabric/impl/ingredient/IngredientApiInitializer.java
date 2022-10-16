package net.fabricmc.fabric.impl.ingredient;

import org.jetbrains.annotations.ApiStatus;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.ingredient.v1.DefaultCustomIngredients;

@ApiStatus.Internal
public class IngredientApiInitializer implements ModInitializer {
	@Override
	public void onInitialize() {
		// Always register default custom ingredients
		DefaultCustomIngredients.or();
	}
}
