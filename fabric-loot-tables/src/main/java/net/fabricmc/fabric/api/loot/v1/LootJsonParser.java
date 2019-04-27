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

package net.fabricmc.fabric.api.loot.v1;

import com.google.gson.Gson;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Lazy;
import net.minecraft.world.loot.*;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.entry.LootEntry;
import net.minecraft.world.loot.function.LootFunction;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Stream;

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

	private LootJsonParser() {

	}

	private static Gson gson() {
		return GSON.get();
	}

	public static LootSupplier readSupplier(String json) {
		return JsonHelper.deserialize(gson(), json, LootSupplier.class);
	}

	public static LootEntry readEntry(String json) {
		return JsonHelper.deserialize(gson(), json, LootEntry.class);
	}

	public static LootPool readPool(String json) {
		return JsonHelper.deserialize(gson(), json, LootPool.class);
	}

	public static LootFunction readFunction(String json) {
		return JsonHelper.deserialize(gson(), json, LootFunction.class);
	}

	public static LootCondition readCondition(String json) {
		return JsonHelper.deserialize(gson(), json, LootCondition.class);
	}
}
