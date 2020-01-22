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

package net.fabricmc.fabric.api.loot.v1;

import java.io.Reader;
import java.lang.reflect.Field;
import java.util.stream.Stream;

import com.google.gson.Gson;

import net.minecraft.util.JsonHelper;
import net.minecraft.util.Lazy;
import net.minecraft.loot.LootManager;

public final class LootJsonParser {
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

	private LootJsonParser() { }

	public static <T> T read(Reader json, Class<T> c) {
		return JsonHelper.deserialize(GSON.get(), json, c);
	}

	public static <T> T read(String json, Class<T> c) {
		return JsonHelper.deserialize(GSON.get(), json, c);
	}
}
