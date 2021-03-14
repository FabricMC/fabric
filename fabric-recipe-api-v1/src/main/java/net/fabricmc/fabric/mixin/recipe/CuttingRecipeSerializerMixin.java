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

package net.fabricmc.fabric.mixin.recipe;

import com.google.gson.JsonObject;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.data.server.recipe.SingleItemRecipeJsonFactory.SingleItemRecipeJsonProvider;
import net.minecraft.recipe.CuttingRecipe;

import net.fabricmc.fabric.api.recipe.v1.serializer.FabricRecipeSerializer;

@Mixin(CuttingRecipe.Serializer.class)
public abstract class CuttingRecipeSerializerMixin<T extends CuttingRecipe> implements FabricRecipeSerializer<T> {
	@Override
	public JsonObject toJson(T recipe) {
		return new SingleItemRecipeJsonProvider(recipe.getId(), this, recipe.getGroup(),
				recipe.getPreviewInputs().get(0), recipe.getOutput().getItem(), recipe.getOutput().getCount(),
				null, null)
				.toJson();
	}
}
