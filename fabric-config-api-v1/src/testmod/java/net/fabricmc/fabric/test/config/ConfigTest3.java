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

package net.fabricmc.fabric.test.config;

import org.jetbrains.annotations.NotNull;

import net.fabricmc.fabric.api.config.v1.FabricSaveTypes;
import net.fabricmc.loader.api.config.ConfigSerializer;
import net.fabricmc.loader.api.config.SaveType;
import net.fabricmc.loader.api.config.data.DataCollector;
import net.fabricmc.loader.api.config.data.DataType;
import net.fabricmc.loader.api.config.entrypoint.ConfigInitializer;
import net.fabricmc.loader.api.config.serialization.TomlSerializer;
import net.fabricmc.loader.api.config.util.Array;
import net.fabricmc.loader.api.config.value.ConfigValueCollector;
import net.fabricmc.loader.api.config.value.ValueKey;

public class ConfigTest3 implements ConfigInitializer {
	public static final ValueKey<Integer> MY_FAVORITE_NUMBER = new ValueKey.Builder<>(() -> 7)
			.with(new Bounds.Int(0, 10))
			.with(DataType.COMMENT, "Like seriously, all other numbers suck.")
			.build();

	public static final ValueKey<String> MY_FAVORITE_FRUIT = new ValueKey.Builder<>(() -> "Strawberry")
			.build();

	public static final ValueKey<Array<String>> MY_FAVORITE_CITIES = new ValueKey.Builder<>(() -> {
		return new Array<>(String.class, () -> "(none)");
	}).build();

	@Override
	public @NotNull ConfigSerializer getSerializer() {
		return TomlSerializer.INSTANCE;
	}

	@Override
	public @NotNull SaveType getSaveType() {
		return FabricSaveTypes.LEVEL;
	}

	@Override
	public @NotNull String getName() {
		return "config3";
	}

	@Override
	public void addConfigValues(@NotNull ConfigValueCollector collector) {
		collector.addConfigValue(MY_FAVORITE_NUMBER, "favorite_number");
		collector.addConfigValue(MY_FAVORITE_FRUIT, "favorite_fruit");
		collector.addConfigValue(MY_FAVORITE_CITIES, "favorite_cities");

		for (int i = 0; i < 10; ++i) {
			collector.addConfigValue(new ValueKey.Builder<>(() -> 0).build(), "test", "value" + i);
		}
	}

	@Override
	public void addConfigData(@NotNull DataCollector collector) {
		collector.add(DataType.COMMENT,
				"This is a great config file",
				"Wanna know why it's so great?\nBECAUSE I SAID SO",
				"Don't believe me?",
				"WELL TOO BAD."
		);
	}
}
