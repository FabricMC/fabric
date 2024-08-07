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

package net.fabricmc.fabric.impl.modprotocol;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class TextUtil {
	private static final Map<String, String> FALLBACK_TRANSLATIONS = new HashMap<>();

	static {
		try {
			Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(ModProtocolInit.MOD_ID);
			Optional<Path> path = container.get().findPath("assets/fabric-mod-protocol-api-v1/lang/en_us.json");
			JsonObject lang = JsonParser.parseString(Files.readString(path.get())).getAsJsonObject();

			for (String key : lang.keySet()) {
				FALLBACK_TRANSLATIONS.put(key, lang.get(key).getAsString());
			}
		} catch (Throwable e) {
			ModProtocolInit.LOGGER.error("Failed to load translation file!", e);
		}
	}

	public static MutableText translatable(String key, Object... args) {
		return Text.translatableWithFallback(key, FALLBACK_TRANSLATIONS.get(key), args);
	}

	public static MutableText translatable(String key) {
		return Text.translatableWithFallback(key, FALLBACK_TRANSLATIONS.get(key));
	}
}
