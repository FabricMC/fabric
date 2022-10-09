package net.fabricmc.fabric.impl.ingredient.builtin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import net.fabricmc.fabric.api.ingredient.v1.CustomIngredient;
import net.fabricmc.fabric.api.ingredient.v1.CustomIngredientSerializer;

@ApiStatus.Internal
public class OrIngredient implements CustomIngredient {
	private final Ingredient[] ingredients;

	public OrIngredient(Ingredient[] ingredients) {
		this.ingredients = ingredients;
	}

	@Override
	public boolean matchesStack(ItemStack stack) {
		for (Ingredient ingredient : ingredients) {
			if (ingredient.test(stack)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public ItemStack[] getPreviewStacks() {
		List<ItemStack> previewStacks = new ArrayList<>();

		for (Ingredient ingredient : ingredients) {
			previewStacks.addAll(Arrays.asList(ingredient.getMatchingStacks()));
		}

		return previewStacks.toArray(ItemStack[]::new);
	}

	@Override
	public boolean ignoresNbt() {
		for (Ingredient ingredient : ingredients) {
			if (!ingredient.ignoresNbt()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public CustomIngredientSerializer<?> getSerializer() {
		return null;
	}

	public static class Serializer implements CustomIngredientSerializer<OrIngredient> {
		public static final Serializer INSTANCE = new Serializer();

		private final Identifier identifier = new Identifier("fabric", "or");

		@Override
		public Identifier getIdentifier() {
			return identifier;
		}

		@Override
		public OrIngredient read(JsonObject json) {
			JsonArray values = JsonHelper.getArray(json, "values");
			Ingredient[] ingredients = new Ingredient[values.size()];

			for (int i = 0; i < values.size(); i++) {
				ingredients[i] = Ingredient.fromJson(values.get(i));
			}

			return new OrIngredient(ingredients);
		}

		@Override
		public void write(JsonObject json, OrIngredient ingredient) {
			JsonArray values = new JsonArray();

			for (Ingredient value : ingredient.ingredients) {
				values.add(value.toJson());
			}

			json.add("values", values);
		}

		@Override
		public OrIngredient read(PacketByteBuf buf) {
			int size = buf.readVarInt();
			Ingredient[] ingredients = new Ingredient[size];

			for (int i = 0; i < size; i++) {
				ingredients[i] = Ingredient.fromPacket(buf);
			}

			return new OrIngredient(ingredients);
		}

		@Override
		public void write(PacketByteBuf buf, OrIngredient ingredient) {
			buf.writeVarInt(ingredient.ingredients.length);

			for (Ingredient value : ingredient.ingredients) {
				value.write(buf);
			}
		}
	}
}
