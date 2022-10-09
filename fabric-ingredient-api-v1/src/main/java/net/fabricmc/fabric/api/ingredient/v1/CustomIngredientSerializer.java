package net.fabricmc.fabric.api.ingredient.v1;

import com.google.gson.JsonObject;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public interface CustomIngredientSerializer<T extends CustomIngredient> {
	Identifier getIdentifier(); // TODO: should this be moved to CustomIngredients' registry instead?

	T read(JsonObject json);
	void write(JsonObject json, T ingredient);

	T read(PacketByteBuf buf);
	void write(PacketByteBuf buf, T ingredient);
}
