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

package net.fabricmc.fabric.mixin.conditionalrecipe;

import java.util.Map;

import com.google.gson.JsonElement;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import net.fabricmc.fabric.api.conditionalrecipe.v1.RecipeConditions;

@Mixin(RecipeManager.class)
public class MixinRecipeManager {
	@Shadow
	@Final
	private static Logger LOGGER;

	@Inject(method = "apply", at = @At("HEAD"))
	private void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
		map.entrySet().removeIf(this::processConditions);
	}

	private boolean processConditions(Map.Entry<Identifier, JsonElement> entry) {
		JsonElement element = entry.getValue();

		if (!element.isJsonObject() || !element.getAsJsonObject().has("conditions")) {
			return false;
		}

		JsonElement conditions = element.getAsJsonObject().get("conditions");
		boolean evaluate = true;

		try {
			evaluate = RecipeConditions.evaluate(entry.getKey(), conditions);
		} catch (Exception e) {
			LOGGER.error("Failed to evaluate conditions for {}: {}", entry.getKey(), conditions);
		}

		return !evaluate;
	}
}
