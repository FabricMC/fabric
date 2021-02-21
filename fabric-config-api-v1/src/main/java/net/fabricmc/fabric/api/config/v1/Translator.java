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

package net.fabricmc.fabric.api.config.v1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.impl.config.lang.Language;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Utility class for translating things on the dedicated server and before there is an instance of {@link MinecraftClient} available.
 */
public class Translator {
	private static final Logger LOGGER = LogManager.getLogger("Fabric|Translator");

	private static final Pattern ARG_FORMAT = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

	private static final Map<String, Language> LANGUAGES = new HashMap<>();

	static {
		FabricLoader.getInstance().getAllMods().forEach(modContainer -> {
			try {
				if (!Files.exists(modContainer.getPath("assets"))) return;

				Files.walk(modContainer.getPath("assets")).forEach(path -> {
					if (path.toString().matches(".*lang.[a-z_]+(\\.json)$")) {
						String langCode = path.getFileName().toString().split("\\.")[0];

						try {
							LANGUAGES.computeIfAbsent(langCode, Language::new).add(modContainer.getMetadata().getId(), path);
						} catch (IOException e) {
							LOGGER.error(e.getMessage());
						}
					}
				});
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}
		});
	}

	private static @NotNull String getLanguage() {
		FabricLoader loader = FabricLoader.getInstance();
		Path gameDir = loader.getGameDir().normalize();

		if (loader.getEnvironmentType() == EnvType.CLIENT) {
			try {
				Path path = gameDir.resolve("options.txt");

				if (Files.exists(path)) {
					for (String line : Files.readAllLines(path)) {
						if (line.matches("lang:[a-z_]+")) {
							return line.split(":", 2)[1];
						}
					}
				}
			} catch (IOException e) {
				LOGGER.error("Error loading lang options on client: {}", e.getMessage());
			}
		} else {
			Path options = gameDir.resolve("lang.txt");

			if (Files.exists(options)) {
				try {
					for (String line : Files.readAllLines(gameDir.resolve("lang.txt"))) {
						if (line.matches("lang:[a-z_]+")) {
							return line.split(":", 2)[1];
						}
					}
				} catch (IOException e) {
					LOGGER.error("Error loading lang options on server: {}", e.getMessage());
				}
			} else {
				try {
					Files.write(options, "lang:en_us".getBytes(StandardCharsets.UTF_8));
				} catch (IOException e) {
					LOGGER.error("Error saving lang options on server: {}", e.getMessage());
				}
			}
		}

		return "en_us";
	}

	public static @Nullable String translate(@NotNull String key, Object... args) {
		String lang = getLanguage();

		@Nullable String translation = LANGUAGES.getOrDefault(lang, LANGUAGES.get(lang)).translate(key);

		if (translation == null) return null;

		Matcher matcher = ARG_FORMAT.matcher(translation);

		try {
			int i = 0;

			int j;
			int l;

			StringBuilder builder = new StringBuilder();

			for (j = 0; matcher.find(j); j = l) {
				int k = matcher.start();
				l = matcher.end();
				String string2;

				if (k > j) {
					string2 = translation.substring(j, k);

					if (string2.indexOf(37) != -1) {
						throw new IllegalArgumentException();
					}

					builder.append(string2);
				}

				string2 = matcher.group(2);
				String string3 = translation.substring(k, l);

				if ("%".equals(string2) && "%%".equals(string3)) {
					builder.append("%");
				} else {
					if (!"s".equals(string2)) {
						throw new RuntimeException("Unsupported format: '" + string3 + "'");
					}

					String string4 = matcher.group(1);
					int m = string4 != null ? Integer.parseInt(string4) - 1 : i++;

					if (m < args.length) {
						builder.append(getArg(key, args, m));
					}
				}
			}

			if (j < translation.length()) {
				String string = translation.substring(j);

				if (string.indexOf(37) != -1) {
					throw new IllegalArgumentException();
				}

				builder.append(string);
			}

			return builder.toString();
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		}
	}

	private static String getArg(@NotNull String key, Object[] args, int index) {
		if (index >= args.length) {
			throw new RuntimeException(String.format("Failed to translate key \"%s\": couldn't get arg %d from args %s", key, index, Arrays.toString(args)));
		} else {
			Object object = args[index];
			return object == null ? "null" : object.toString();
		}
	}

	public static @NotNull Collection<String> getComments(String key) {
		return LANGUAGES.get(getLanguage()).getComments(key);
	}
}
