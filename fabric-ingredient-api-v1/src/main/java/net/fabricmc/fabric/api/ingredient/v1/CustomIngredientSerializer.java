package net.fabricmc.fabric.api.ingredient.v1;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.ingredient.CustomIngredientImpl;

/**
 * Serializer for a {@link CustomIngredient}.
 *
 * <p>All instances must be registered using {@link #register} for deserialization to work.
 *
 * @param <T> the type of the custom ingredient
 */
public interface CustomIngredientSerializer<T extends CustomIngredient> {
	/**
	 * Register a custom ingredient serializer, using the {@linkplain CustomIngredientSerializer#getIdentifier() serializer's identifier}.
	 *
	 * @throws IllegalArgumentException if the serializer is already registered
	 */
	static void register(CustomIngredientSerializer<?> serializer) {
		CustomIngredientImpl.registerSerializer(serializer);
	}

	/**
	 * {@return the custom ingredient serializer registered with the given identifier, or {@code null} if none is registered.}
	 */
	@Nullable
	static CustomIngredientSerializer<?> get(Identifier identifier) {
		return CustomIngredientImpl.getSerializer(identifier);
	}

	Identifier getIdentifier();

	T read(JsonObject json);

	void write(JsonObject json, T ingredient);

	T read(PacketByteBuf buf);

	void write(PacketByteBuf buf, T ingredient);
}
