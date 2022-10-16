package net.fabricmc.fabric.api.ingredient.v1;

import org.jetbrains.annotations.Nullable;

public interface FabricIngredient {
	default boolean isCustom() {
		return false;
	}

	@Nullable
	default CustomIngredient getCustomIngredient() {
		return null;
	}

	default boolean requiresTesting() {
		return true;
	}
}
