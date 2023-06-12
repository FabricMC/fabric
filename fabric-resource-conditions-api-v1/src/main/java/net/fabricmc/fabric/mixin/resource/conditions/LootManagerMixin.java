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

package net.fabricmc.fabric.mixin.resource.conditions;

import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.loot.LootDataType;
import net.minecraft.loot.LootManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;

/**
 * Starting with 1.20, LootManager directly implements ResourceReloader.
 */
@Mixin(LootManager.class)
public class LootManagerMixin {
	// forEach in load method
	@Inject(method = "method_51195", at = @At("HEAD"), cancellable = true)
	private static void applyResourceConditions(LootDataType lootDataType, Map map, Identifier id, JsonElement json, CallbackInfo ci) {
		if (json.isJsonObject()) {
			JsonObject obj = json.getAsJsonObject();

			if (obj.has(ResourceConditions.CONDITIONS_KEY)) {
				boolean matched = ResourceConditions.objectMatchesConditions(obj);

				if (!matched) {
					ci.cancel();
				}

				if (ResourceConditionsImpl.LOGGER.isDebugEnabled()) {
					String verdict = matched ? "Allowed" : "Rejected";
					ResourceConditionsImpl.LOGGER.debug("{} resource of type {} with id {}", verdict, lootDataType.getId(), id);
				}
			}
		}
	}
}
