package net.fabricmc.fabric.impl.ingredient;

import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;

import net.fabricmc.fabric.api.ingredient.v1.CustomIngredient;

@ApiStatus.Internal
public class CustomIngredientImpl extends Ingredient {
	public static final String TYPE_KEY = "fabric:type";
	public static final int PACKET_MARKER = -1;

	private final CustomIngredient customIngredient;

	public CustomIngredientImpl(CustomIngredient customIngredient) {
		super(Stream.empty());

		this.customIngredient = customIngredient;
	}

	@Override
	public boolean isCustom() {
		return true;
	}

	@Override
	public CustomIngredient getCustomIngredient() {
		return customIngredient;
	}

	@Override
	public boolean ignoresNbt() {
		return customIngredient.ignoresNbt();
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && customIngredient.matchesStack(stack);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(PACKET_MARKER);
		buf.writeIdentifier(customIngredient.getSerializer().getIdentifier());
		customIngredient.getSerializer().write(buf, coerceIngredient());
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty(TYPE_KEY, customIngredient.getSerializer().getIdentifier().toString());
		customIngredient.getSerializer().write(json, coerceIngredient());
		return json;
	}

	// TODO: isEmpty might require an override? need to check when it's used (before or after tags are bound?)

	private <T> T coerceIngredient() {
		return (T) customIngredient;
	}
}
