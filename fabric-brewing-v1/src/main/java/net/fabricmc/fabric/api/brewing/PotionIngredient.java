package net.fabricmc.fabric.api.brewing;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.fabric.impl.brewing.PotionIngredientImpl;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public interface PotionIngredient {
	boolean isIngredient();
	boolean isPotion();
	Ingredient asIngredient();
	Potion asPotion();
	Identifier getPotionType();
	boolean test(ItemStack stack);
	void write(PacketByteBuf buf);

	static PotionIngredient fromJson(JsonElement json) { return fromJson(json, false); }
	static PotionIngredient fromJson(JsonElement json, boolean forceSingle) {
		if(json != null && !json.isJsonNull()) {
			if(json.isJsonObject()) {
				JsonObject obj = json.getAsJsonObject();
				if(forceSingle && JsonHelper.hasString(obj, "tag"))
					throw new JsonSyntaxException("Cannot use a tag for recipe output");

				return JsonHelper.hasString(obj, "potion")
					? new PotionIngredientImpl(Registry.POTION.get(new Identifier(JsonHelper.getString(obj, "potion"))),
					new Identifier(JsonHelper.getString(obj, "type", "normal")))
					: new PotionIngredientImpl(Ingredient.fromJson(obj));
			} else
				throw new JsonSyntaxException("Expected item to be object");
		} else
			throw new JsonSyntaxException("Item cannot be null");
	}

	static PotionIngredient fromPacket(PacketByteBuf buf) {
		boolean isPotion = buf.readBoolean();
		if(!isPotion) return new PotionIngredientImpl(Ingredient.fromPacket(buf));
		return new PotionIngredientImpl(Registry.POTION.get(new Identifier(buf.readString())), new Identifier(buf.readString()));
	}
}
