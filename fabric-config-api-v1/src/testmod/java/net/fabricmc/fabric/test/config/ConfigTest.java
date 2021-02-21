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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;

import net.fabricmc.fabric.api.config.v1.GsonSerializer;
import net.fabricmc.loader.api.config.data.Constraint;
import net.fabricmc.loader.api.config.entrypoint.ConfigInitializer;
import net.fabricmc.loader.api.config.serialization.TomlSerializer;
import net.fabricmc.loader.api.config.util.Table;
import net.fabricmc.loader.api.config.serialization.AbstractTreeSerializer;
import net.fabricmc.fabric.api.config.v1.DataTypes;
import net.fabricmc.fabric.api.config.v1.FabricSaveTypes;
import net.fabricmc.fabric.api.config.v1.SyncType;
import net.fabricmc.loader.api.config.ConfigSerializer;
import net.fabricmc.loader.api.config.SaveType;
import net.fabricmc.loader.api.config.data.DataCollector;
import net.fabricmc.loader.api.config.data.DataType;
import net.fabricmc.loader.api.config.value.ConfigValueCollector;
import net.fabricmc.loader.api.config.value.ValueKey;

public class ConfigTest implements ConfigInitializer {
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

	public static final ValueKey<Integer> MY_FAVORITE_NUMBER = new ValueKey.Builder<>(() -> 7)
			.with(new Bounds.Int(0, 10))
			.with(DataType.COMMENT, "Mwa", "ha", "ha", "ha", "ha")
			.build();

	public static final ValueKey<Integer> MY_FAVORITE_NUMBER2 = new ValueKey.Builder<>(() -> 7)
			.build();

	public static final ValueKey<Integer> MY_FAVORITE_NUMBER3 = new ValueKey.Builder<>(() -> 7)
			.build();

	public static final ValueKey<Integer> MY_FAVORITE_NUMBER4 = new ValueKey.Builder<>(() -> 7)
			.build();

	public static final ValueKey<String> MY_FAVORITE_FRUIT = new ValueKey.Builder<>(() -> "Strawberry")
			.with(DataType.COMMENT, "So much delicious flavor!")
			.with(DataTypes.SYNC_TYPE, SyncType.INFO)
			.build();

	public static final ValueKey<Color> MY_FAVORITE_COLOR = new ValueKey.Builder<>(() -> new Color(0xFFFFFF))
			.with(NO_ALPHA)
				.with(DataTypes.SYNC_TYPE, SyncType.P2P, SyncType.INFO)
			.build();

	public static final ValueKey<Table<Color>> TAGS = new ValueKey.CollectionBuilder<>(() -> new Table<>(Color.class, () -> new Color(-1)))
			.constraint(NO_ALPHA)
			.with(DataTypes.SYNC_TYPE, SyncType.P2P, SyncType.INFO)
			.build();

	@Override
	public @NotNull ConfigSerializer getSerializer() {
		return CustomTomlSerializer.INSTANCE;
	}

	@Override
	public @NotNull SaveType getSaveType() {
		return FabricSaveTypes.USER;
	}

	@Override
	public void addConfigValues(@NotNull ConfigValueCollector collector) {
		collector.addConfigValue(MY_FAVORITE_NUMBER, "favorite_number");
		collector.addConfigValue(MY_FAVORITE_NUMBER2, "favorite_number_2");
		collector.addConfigValue(MY_FAVORITE_NUMBER3, "favorite_number_3");
		collector.addConfigValue(MY_FAVORITE_NUMBER4, "favorite_number_4");
		collector.addConfigValue(MY_FAVORITE_FRUIT, "favorite_fruit");
		collector.addConfigValue(MY_FAVORITE_COLOR, "favorite_color");
		collector.addConfigValue(TAGS, "tags");
	}

	@Override
	public void addConfigData(@NotNull DataCollector collector) {
		collector.add(DataType.COMMENT, "This is a comment");
		collector.add(DataType.COMMENT, "This is another one");
		collector.add(DataType.COMMENT, "This is a third comment");
	}

	private static class CustomJsonSerializer extends GsonSerializer {
		private static final ConfigSerializer INSTANCE = new CustomJsonSerializer();

		CustomJsonSerializer() {
			super(new Gson());
			this.addSerializer(Color.class, new ColorSerializer());
		}
	}

	private static class ColorSerializer implements AbstractTreeSerializer.ValueSerializer<JsonElement, JsonPrimitive, Color> {
		@Override
		public JsonPrimitive serialize(Color value) {
			if (value.value == -1) {
				return new JsonPrimitive("0xFFFFFFFF");
			} else {
				return new JsonPrimitive("0x" + Integer.toUnsignedString(value.value, 16).toUpperCase(Locale.ROOT));
			}
		}

		@Override
		public Color deserialize(JsonElement representation) {
			if (representation.getAsString().equalsIgnoreCase("0xFFFFFFFF")) {
				return new Color(-1);
			} else {
				return new Color(Integer.parseUnsignedInt(representation.getAsString().substring(2), 16));
			}
		}
	}

	private static class CustomTomlSerializer extends TomlSerializer {
		static final ConfigSerializer INSTANCE = new CustomTomlSerializer();

		private CustomTomlSerializer() {
			this.addSerializer(Color.class, ColorSerializer.INSTANCE);
		}

		static class ColorSerializer implements ValueSerializer<Color> {
			public static final ColorSerializer INSTANCE = new ColorSerializer();

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
}
