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

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import net.fabricmc.loader.api.config.entrypoint.Config;
import net.fabricmc.fabric.api.config.v1.GsonSerializer;
import net.fabricmc.loader.api.config.util.Array;
import net.fabricmc.loader.api.config.data.DataCollector;
import net.fabricmc.loader.api.config.data.DataType;
import net.fabricmc.fabric.api.config.v1.FabricSaveTypes;
import net.fabricmc.loader.api.config.value.ValueKey;
import net.fabricmc.loader.api.config.data.SaveType;
import net.fabricmc.loader.api.config.serialization.ConfigSerializer;
import net.fabricmc.fabric.api.config.v1.FabricDataTypes;
import net.fabricmc.fabric.api.config.v1.SyncType;

public class LevelConfigTest extends Config<JsonObject> {
	public static final ValueKey<String> MY_FAVORITE_FRUIT = new ValueKey.Builder<>(() -> "Strawberry")
			.build();

	public static final ValueKey<Array<String>> MY_FAVORITE_CITIES = new ValueKey.Builder<>(() ->
			new Array<>(String.class, () -> "(none)"))
			.with(FabricDataTypes.SYNC_TYPE, SyncType.INFO)
			.build();

	@Override
	public @NotNull ConfigSerializer<JsonObject> getSerializer() {
		return GsonSerializer.DEFAULT;
	}

	@Override
	public @NotNull SaveType getSaveType() {
		return FabricSaveTypes.LEVEL;
	}

	@Override
	public @NotNull String getName() {
		return "level_config";
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
