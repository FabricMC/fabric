/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.impl.brewing;

import com.google.gson.JsonObject;
import net.fabricmc.api.ModInitializer;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class FabricBrewingInit implements ModInitializer {
	public void onInitialize() {
		Registry.register(Registry.RECIPE_TYPE, "fabric:brewing", BREWING_RECIPE_TYPE);
		Registry.register(Registry.RECIPE_SERIALIZER, "fabric:brewing", BREWING_RECIPE_SERIALIZER);
	}

	public static final RecipeType<BrewingRecipe> BREWING_RECIPE_TYPE = new RecipeType<BrewingRecipe>() {
		public String toString() { return "fabric:brewing"; }
	};

	public static final RecipeSerializer<BrewingRecipe> BREWING_RECIPE_SERIALIZER = new RecipeSerializer<BrewingRecipe>() {
		public BrewingRecipe read(Identifier id, JsonObject obj) {
			return new BrewingRecipe(
				id, JsonHelper.getString(obj, "group", ""),
				PotionIngredient.fromJson(JsonHelper.getObject(obj, "input")),
				PotionIngredient.fromJson(JsonHelper.getObject(obj, "base")),
				PotionIngredient.fromJson(JsonHelper.getObject(obj, "output"), true)
			);
		}

		public BrewingRecipe read(Identifier id, PacketByteBuf buf) {
			return new BrewingRecipe(
				id, buf.readString(32767),
				PotionIngredient.fromPacket(buf),
				PotionIngredient.fromPacket(buf),
				PotionIngredient.fromPacket(buf)
			);
		}

		public void write(PacketByteBuf buf, BrewingRecipe recipe) {
			buf.writeString(recipe.getGroup());
			recipe.getInput().write(buf);
			recipe.getBasePotion().write(buf);
			recipe.getOutputPotionIngredient().write(buf);
		}
	};
}
