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

package net.fabricmc.fabric.mixin.server.language;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonParseException;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.util.Language;

import net.fabricmc.fabric.impl.server.language.ServerTranslationEntry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

/**
 * Loads all mods' en_us lang file into the Language instance on the dedicated server with a basic strategy for
 * resolving collisions.
 */
@Mixin(Language.class)
public class MixinLanguage {
	@Shadow
	@Final
	private static Logger LOGGER;
	@Shadow
	@Final
	private static String DEFAULT_LANGUAGE;

	@Shadow
	private static void load(InputStream inputStream, BiConsumer<String, String> entryConsumer) {
	}

	private static void loadModLangFile(ModContainer modContainer, BiConsumer<String, String> biConsumer) {
		Path path = modContainer.getPath("assets/" + modContainer.getMetadata().getId() + "/lang/" + DEFAULT_LANGUAGE + ".json");

		if (!Files.exists(path)) {
			return;
		}

		try (InputStream inputStream = Files.newInputStream(path)) {
			load(inputStream, biConsumer);
		} catch (JsonParseException | IOException e) {
			LOGGER.error("Couldn't read strings from " + path, e);
		}
	}

	@Inject(method = "create", cancellable = true, at = @At("HEAD"))
	private static void create(CallbackInfoReturnable<Language> cir) {
		Map<String, List<ServerTranslationEntry>> translations = new HashMap<>();

		for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			loadModLangFile(mod, (k, v) -> {
				if (!translations.containsKey(k)) translations.put(k, new ArrayList<>());
				translations.get(k).add(new ServerTranslationEntry(mod.getMetadata(), k, v));
			});
		}

		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

		for (Map.Entry<String, List<ServerTranslationEntry>> keyEntry : translations.entrySet()) {
			if (keyEntry.getValue().size() == 1) {
				// Only one value provided for this key
				builder.put(keyEntry.getKey(), keyEntry.getValue().get(0).getValue());
			} else {
				// Collision occurred for this key.
				// Strategy: Resolve collision by choosing value provided by mod that depends on the greatest number of
				// other mods in this collision cluster, according to mod metadata.
				// Rationale: The mod that intends to overwrite another mod's keys is more likely to declare that mod
				// as a dependency.
				Set<String> clusterIds = keyEntry.getValue().stream().map(ServerTranslationEntry::getModId).collect(Collectors.toSet());
				ServerTranslationEntry pickedEntry = Collections.max(keyEntry.getValue(),
						Comparator.comparingInt(entry -> entry.getDependencyIntersectionSize(clusterIds)));
				builder.put(keyEntry.getKey(), pickedEntry.getValue());
			}
		}

		final Map<String, String> map = builder.build();
		cir.setReturnValue(new Language() {
			@Override
			public String get(String key) {
				return map.getOrDefault(key, key);
			}

			@Override
			public boolean hasTranslation(String key) {
				return map.containsKey(key);
			}

			@Override
			public boolean isRightToLeft() {
				return false;
			}

			@Override
			public OrderedText reorder(StringVisitable text) {
				return visitor -> text.visit((style, string) -> TextVisitFactory.visitFormatted(string, style, visitor) ? Optional.empty() : StringVisitable.TERMINATE_VISIT, Style.EMPTY).isPresent();
			}
		});
	}
}
