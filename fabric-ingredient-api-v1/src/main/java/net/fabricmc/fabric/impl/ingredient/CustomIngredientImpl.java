package net.fabricmc.fabric.impl.ingredient;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.ingredient.v1.CustomIngredient;
import net.fabricmc.fabric.api.ingredient.v1.CustomIngredientSerializer;

@ApiStatus.Internal
public class CustomIngredientImpl extends Ingredient {
	// Static helpers used by the API

	public static final String TYPE_KEY = "fabric:type";
	public static final int PACKET_MARKER = -1;

	private static final Map<Identifier, CustomIngredientSerializer<?>> REGISTERED_SERIALIZERS = new ConcurrentHashMap<>();

	public static void registerSerializer(CustomIngredientSerializer<?> serializer) {
		Objects.requireNonNull(serializer.getIdentifier(), "CustomIngredientSerializer identifier may not be null.");

		if (REGISTERED_SERIALIZERS.putIfAbsent(serializer.getIdentifier(), serializer) != null) {
			throw new IllegalArgumentException("CustomIngredientSerializer with identifier " + serializer.getIdentifier() + " already registered.");
		}
	}

	@Nullable
	public static CustomIngredientSerializer<?> getSerializer(Identifier identifier) {
		Objects.requireNonNull(identifier, "Identifier may not be null.");

		return REGISTERED_SERIALIZERS.get(identifier);
	}

	// Actual custom ingredient logic

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
	public boolean requiresTesting() {
		return customIngredient.requiresTesting();
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null && customIngredient.test(stack);
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

	@Override
	public boolean isEmpty() {
		// We don't want to resolve the matching stacks,
		// as this might cause the ingredient to use outdated tags when it's done too early.
		// So we just return false when the matching stacks haven't been resolved yet (i.e. when the field is null).
		return matchingStacks == null || matchingStacks.length == 0;
	}

	private <T> T coerceIngredient() {
		return (T) customIngredient;
	}
}
