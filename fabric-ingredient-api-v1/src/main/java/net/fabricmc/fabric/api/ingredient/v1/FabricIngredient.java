package net.fabricmc.fabric.api.ingredient.v1;

import org.jetbrains.annotations.Nullable;

// TODO: ignoresNbt is definitely useful, but what about the other two?
// TODO: getCustomIngredient might return weird results for "OR" ingredients
public interface FabricIngredient {
	default boolean isCustom() {
		return false;
	}

	@Nullable
	default CustomIngredient getCustomIngredient() {
		return null;
	}

	default boolean ignoresNbt() {
		return true;
	}
}
