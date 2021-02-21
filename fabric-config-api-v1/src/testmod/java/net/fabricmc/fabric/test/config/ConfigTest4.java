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

import java.util.Map;

import org.jetbrains.annotations.NotNull;

import net.fabricmc.fabric.api.config.v1.Config;
import net.fabricmc.loader.api.config.SaveType;
import net.fabricmc.loader.api.config.serialization.TomlSerializer;
import net.fabricmc.loader.api.config.serialization.toml.TomlElement;
import net.fabricmc.loader.api.config.util.Array;
import net.fabricmc.loader.api.config.value.ValueKey;

public class ConfigTest4 extends Config<Map<String, TomlElement>> {
	public static final ValueKey<Integer> TEST_INT = value(() -> 0);
	public static final ValueKey<Array<String>> TEST_STR_ARRAY = array(() -> "(none)", "1", "2", "3");

	@Override
	public @NotNull String getName() {
		return "fancy_test";
	}

	@Override
	public @NotNull TomlSerializer getSerializer() {
		return TomlSerializer.INSTANCE;
	}

	@Override
	public @NotNull SaveType getSaveType() {
		return SaveType.ROOT;
	}
}
