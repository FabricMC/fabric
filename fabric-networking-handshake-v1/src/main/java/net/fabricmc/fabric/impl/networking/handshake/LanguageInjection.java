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

package net.fabricmc.fabric.impl.networking.handshake;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.JsonHelper;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

// todo move to resources maybe
public final class LanguageInjection {
	private static final Logger LOGGER = LogManager.getLogger();

	private LanguageInjection() {
	}

	public static void injectHandshakeInfo(Map<String, String> translations, Pattern argPattern) {
		ModContainer mod = FabricLoader.getInstance().getModContainer(HandshakeMod.ID).orElseThrow(() -> new IllegalStateException("Cannot find handshake mod!"));
		Path langFile = mod.getPath("assets").resolve(mod.getMetadata().getId()).resolve("lang").resolve("en_us.json");

		if (!Files.exists(langFile)) {
			return;
		}

		try (BufferedReader reader = Files.newBufferedReader(langFile)) {
			for (Map.Entry<String, JsonElement> element : JsonHelper.deserialize(reader).entrySet()) {
				String key = element.getKey();
				String translated = argPattern.matcher(JsonHelper.asString(element.getValue(), key)).replaceAll("%$1s");
				translations.put(key, translated);
			}
		} catch (IOException | JsonParseException ex) {
			LOGGER.error("Couldn't read strings from /assets/{}/lang/en_us.json", mod.getMetadata().getId(), ex);
		}
	}
}
