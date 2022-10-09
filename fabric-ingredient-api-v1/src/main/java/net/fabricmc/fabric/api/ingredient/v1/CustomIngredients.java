package net.fabricmc.fabric.api.ingredient.v1;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.Nullable;

import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.ingredient.builtin.OrIngredient;

// TODO: add entrypoint that loads this class
// TODO: more default ingredients
public class CustomIngredients {
	private static final Map<Identifier, CustomIngredientSerializer<?>> REGISTERED_SERIALIZERS = new ConcurrentHashMap<>();

	public static void register(CustomIngredientSerializer<?> customIngredientSerializer) {
		Objects.requireNonNull(customIngredientSerializer.getIdentifier(), "Serializer identifier may not be null.");

		if (REGISTERED_SERIALIZERS.putIfAbsent(customIngredientSerializer.getIdentifier(), customIngredientSerializer) != null) {
			throw new IllegalArgumentException("Serializer with identifier " + customIngredientSerializer.getIdentifier() + " already registered.");
		}
	}

	@Nullable
	public static CustomIngredientSerializer<?> get(Identifier identifier) {
		Objects.requireNonNull(identifier, "Identifier may not be null.");

		return REGISTERED_SERIALIZERS.get(identifier);
	}

	public static Ingredient or(Ingredient... ingredients) {
		for (Ingredient ing : ingredients) Objects.requireNonNull(ing);

		return new OrIngredient(ingredients).toVanilla();
	}

	static {
		register(OrIngredient.Serializer.INSTANCE);
	}

	private CustomIngredients() {
	}
}
