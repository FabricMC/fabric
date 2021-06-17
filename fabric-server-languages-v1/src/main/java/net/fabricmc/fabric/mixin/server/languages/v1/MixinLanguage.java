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

package net.fabricmc.fabric.mixin.server.languages.v1;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.function.BiConsumer;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonParseException;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.util.Language;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

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

	@Inject(method = "create", locals = LocalCapture.CAPTURE_FAILSOFT, at = @At(value = "INVOKE", remap = false, target = "Lcom/google/common/collect/ImmutableMap$Builder;build()Lcom/google/common/collect/ImmutableMap;"))
	private static void create(CallbackInfoReturnable<Language> cir, ImmutableMap.Builder<String, String> builder) {
		/*  We must ensure that the keys are de-duplicated because we can't catch the error that might otherwise
		 *  occur when the injected function calls build() on the ImmutableMap builder. So we use our own hash map and
		 *  exclude "minecraft", as the injected function has already loaded those keys at this point.
		 */
		HashMap<String, String> translations = new HashMap<>();

		FabricLoader.getInstance().getAllMods().stream()
				.filter(mc -> !mc.getMetadata().getId().equals("minecraft")).forEach(mc -> {
					loadModLangFile(mc, translations::put);
				});

		builder.putAll(translations);
	}
}
