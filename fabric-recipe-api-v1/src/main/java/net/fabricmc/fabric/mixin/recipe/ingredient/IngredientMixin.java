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

package net.fabricmc.fabric.mixin.recipe.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.fabricmc.fabric.api.recipe.v1.ingredient.FabricIngredient;
import net.fabricmc.fabric.impl.recipe.ingredient.CustomIngredientImpl;
import net.fabricmc.fabric.impl.recipe.ingredient.builtin.AnyIngredient;

@Mixin(Ingredient.class)
public class IngredientMixin implements FabricIngredient {
	/**
	 * Inject right when vanilla detected a json object and check for our custom key.
	 */
	@Inject(
			at = @At(
					value = "INVOKE",
					target = "net/minecraft/recipe/Ingredient.entryFromJson (Lcom/google/gson/JsonObject;)Lnet/minecraft/recipe/Ingredient$Entry;",
					ordinal = 0
			),
			method = "fromJson",
			cancellable = true
	)
	private static void injectFromJson(JsonElement json, CallbackInfoReturnable<Ingredient> cir) {
		JsonObject obj = json.getAsJsonObject();

		if (obj.has(CustomIngredientImpl.TYPE_KEY)) {
			Identifier id = new Identifier(JsonHelper.getString(obj, CustomIngredientImpl.TYPE_KEY));
			CustomIngredientSerializer<?> serializer = CustomIngredientSerializer.get(id);

			if (serializer != null) {
				cir.setReturnValue(serializer.read(obj).toVanilla());
			} else {
				throw new IllegalArgumentException("Unknown custom ingredient type: " + id);
			}
		}
	}

	/**
	 * Throw exception when someone attempts to use our custom key inside an array ingredient.
	 * The {@link AnyIngredient} should be used instead.
	 */
	@Inject(at = @At("HEAD"), method = "entryFromJson")
	private static void injectEntryFromJson(JsonObject obj, CallbackInfoReturnable<?> cir) {
		if (obj.has(CustomIngredientImpl.TYPE_KEY)) {
			throw new IllegalArgumentException("Custom ingredient cannot be used inside an array ingredient. You can replace the array by a fabric:any ingredient.");
		}
	}

	@Inject(
			at = @At("HEAD"),
			method = "fromPacket",
			cancellable = true
	)
	private static void injectFromPacket(PacketByteBuf buf, CallbackInfoReturnable<Ingredient> cir) {
		int index = buf.readerIndex();

		if (buf.readVarInt() == CustomIngredientImpl.PACKET_MARKER) {
			Identifier type = buf.readIdentifier();
			CustomIngredientSerializer<?> serializer = CustomIngredientSerializer.get(type);

			if (serializer == null) {
				throw new IllegalArgumentException("Cannot deserialize custom ingredient of unknown type " + type);
			}

			cir.setReturnValue(serializer.read(buf).toVanilla());
		} else {
			// Reset index for vanilla's normal deserialization logic.
			buf.readerIndex(index);
		}
	}
}
