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

import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.jetbrains.annotations.NotNull;

import net.fabricmc.loader.api.config.entrypoint.Config;
import net.fabricmc.fabric.api.config.v1.FabricDataTypes;
import net.fabricmc.fabric.api.config.v1.FabricSaveTypes;
import net.fabricmc.fabric.api.config.v1.SyncType;
import net.fabricmc.loader.api.config.data.Constraint;
import net.fabricmc.loader.api.config.data.DataCollector;
import net.fabricmc.loader.api.config.data.DataType;
import net.fabricmc.loader.api.config.serialization.TOMLSerializer;
import net.fabricmc.loader.api.config.serialization.toml.TomlElement;
import net.fabricmc.loader.api.config.util.Table;
import net.fabricmc.loader.api.config.value.ValueKey;
import net.fabricmc.loader.api.config.data.SaveType;
import net.fabricmc.loader.api.config.serialization.ConfigSerializer;

public class UserConfigTest extends Config<Map<String, TomlElement>> {
	private static final Random RANDOM = new Random();

	public static int EASY_FIELD_ACCESSIBLE_CONFIG_VALUE;

	public static final ValueKey<Integer> MY_FAVORITE_NUMBER = builder(RANDOM.nextInt(11)).bounds(0, 10)
			.with(DataType.COMMENT, "Like seriously, all other numbers suck.")
			// This can be useful for contexts where the absolute minimal performance impact is required
			// i.e. renderers like Canvas, that might access a config value several thousand times per frame
			.with((oldValue, newValue) -> EASY_FIELD_ACCESSIBLE_CONFIG_VALUE = newValue)
			.with((oldValue, newValue) -> System.out.printf("My favorite number is not %d!%n", newValue))
			.build();

	private static final Constraint<Color> NO_ALPHA = new Constraint<Color>("fabric:bounds/color") {
		@Override
		public boolean passes(Color value) {
			return value.value <= 0xFFFFFF;
		}

		@Override
		public String toString() {
			return super.toString() + "[0 <= value <= 0xFFFFFF]";
		}
	};

	public static final ValueKey<Color> MY_FAVORITE_COLOR = new ValueKey.Builder<>(() -> new Color(RANDOM.nextInt(0xFFFFFF)))
			.with(NO_ALPHA)
			.with(FabricDataTypes.SYNC_TYPE, SyncType.P2P, SyncType.INFO)
			.build();

	public static final ValueKey<Table<Color>> TAGS = new ValueKey.CollectionBuilder<>(() -> new Table<>(Color.class, () -> new Color(-1)))
			.constraint(NO_ALPHA)
			.with(FabricDataTypes.SYNC_TYPE, SyncType.P2P, SyncType.INFO)
			.build();

	@Override
	public @NotNull ConfigSerializer<Map<String, TomlElement>> getSerializer() {
		return TOMLSerializer.INSTANCE;
	}

	@Override
	public @NotNull SaveType getSaveType() {
		return FabricSaveTypes.USER;
	}

	@Override
	public void addConfigData(@NotNull DataCollector collector) {
		collector.add(DataType.COMMENT, "This is a comment");
		collector.add(DataType.COMMENT, "This is another one");
		collector.add(DataType.COMMENT, "This is a third comment");
	}

	@Override
	public @NotNull String getName() {
		return "user_config";
	}

	static {
		TOMLSerializer.INSTANCE.addSerializer(Color.class, new ColorSerializer());
	}

	static class ColorSerializer implements TOMLSerializer.ValueSerializer<Color> {
		@Override
		public Object serialize(Color value) {
			if (value.value == -1) {
				return "0xFFFFFFFF";
			} else {
				return "0x" + Integer.toUnsignedString(value.value, 16).toUpperCase(Locale.ROOT);
			}
		}

		@Override
		public Color deserialize(Object object) {
			String string = (String) object;

			if (string.equalsIgnoreCase("0xFFFFFFFF")) {
				return new Color(-1);
			} else {
				return new Color(Integer.parseUnsignedInt(string.substring(2), 16));
			}
		}
	}
}
