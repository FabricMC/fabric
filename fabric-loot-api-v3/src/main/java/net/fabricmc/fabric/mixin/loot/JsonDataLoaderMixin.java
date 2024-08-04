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

package net.fabricmc.fabric.mixin.loot;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.loot.LootDataType;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.loot.LootUtil;

@Mixin(JsonDataLoader.class)
public class JsonDataLoaderMixin {
	@Inject(method = "load", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/resource/ResourceFinder;toResourceId(Lnet/minecraft/util/Identifier;)Lnet/minecraft/util/Identifier;", shift = At.Shift.AFTER))
	private static void fillSourceMap(ResourceManager manager, String dataType, Gson gson, Map<Identifier, JsonElement> results, CallbackInfo ci, @Local Map.Entry<Identifier, Resource> entry, @Local(ordinal = 1) Identifier id) {
		if (!LootDataType.LOOT_TABLES.registryKey().getValue().getPath().equals(dataType)) return;

		LootUtil.SOURCES.get().put(id, LootUtil.determineSource(entry.getValue()));
	}
}
