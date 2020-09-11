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

package net.fabricmc.fabric.mixin.conditionalresource;

import java.util.Map;

import com.google.gson.JsonElement;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import net.fabricmc.fabric.api.conditionalresource.v1.ResourceConditions;

@Mixin(JsonDataLoader.class)
public class MixinJsonDataLoader {
	@Shadow
	@Final
	private static Logger LOGGER;

	@Inject(method = "prepare", at = @At("RETURN"))
	private void apply(ResourceManager resourceManager, Profiler profiler, CallbackInfoReturnable<Map<Identifier, JsonElement>> cir) {
		cir.getReturnValue().entrySet().removeIf(this::processConditions);
	}

	@Unique
	private boolean processConditions(Map.Entry<Identifier, JsonElement> entry) {
		JsonElement element = entry.getValue();

		if (!element.isJsonObject() || !element.getAsJsonObject().has("conditions")) {
			return false;
		}

		JsonElement conditions = element.getAsJsonObject().get("conditions");
		boolean evaluate = true;

		try {
			evaluate = ResourceConditions.evaluate(entry.getKey(), conditions);
		} catch (Exception e) {
			LOGGER.error("Failed to evaluate conditions for {}: {}", entry.getKey(), conditions);
		}

		return !evaluate;
	}
}
