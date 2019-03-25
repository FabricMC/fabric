/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.api.loot;

import com.google.gson.Gson;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Lazy;
import net.minecraft.world.loot.*;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.entry.LootEntry;
import net.minecraft.world.loot.function.LootFunction;

import java.lang.reflect.Field;
import java.util.stream.Stream;

public final class LootUtilities {
	/* Reading this from LootManager to access all serializers from vanilla. */
	private static final Lazy<Gson> GSON = new Lazy<>(() -> {
		try {
			Field gsonField = Stream.of(LootManager.class.getDeclaredFields())
					.filter(field -> field.getType() == Gson.class)
					.findFirst()
					.orElseThrow(() -> new RuntimeException("Gson not found in LootManager!"));
			gsonField.setAccessible(true);
			return (Gson) gsonField.get(null);
		} catch (Exception e) {
			throw new RuntimeException("Exception while getting Gson instance from LootManager", e);
		}
	});

	private static Gson gson() {
		return GSON.get();
	}

	public static LootSupplier readSupplierFromJson(String json) {
		return JsonHelper.deserialize(gson(), json, LootSupplier.class);
	}

	public static LootEntry readEntryFromJson(String json) {
		return JsonHelper.deserialize(gson(), json, LootEntry.class);
	}

	public static LootPool readPoolFromJson(String json) {
		return JsonHelper.deserialize(gson(), json, LootPool.class);
	}

	public static LootFunction readFunctionFromJson(String json) {
		return JsonHelper.deserialize(gson(), json, LootFunction.class);
	}

	public static LootCondition readConditionFromJson(String json) {
		return JsonHelper.deserialize(gson(), json, LootCondition.class);
	}
}
