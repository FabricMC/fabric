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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.config.serialization.AbstractTreeSerializer;
import net.fabricmc.loader.api.config.util.Array;
import net.fabricmc.loader.api.config.util.Table;

public class GsonSerializer extends AbstractTreeSerializer<JsonElement, JsonObject> {
	public static GsonSerializer DEFAULT = new GsonSerializer(new GsonBuilder().setPrettyPrinting().create());

	private final Gson gson;

	public GsonSerializer(Gson gson) {
		this.gson = gson;

		this.addSerializer(Boolean.class, BooleanSerializer.INSTANCE);
		this.addSerializer(Integer.class, IntSerializer.INSTANCE);
		this.addSerializer(Long.class, LongSerializer.INSTANCE);
		this.addSerializer(String.class, StringSerializer.INSTANCE);
		this.addSerializer(Float.class, FloatSerializer.INSTANCE);
		this.addSerializer(Double.class, DoubleSerializer.INSTANCE);

		this.addSerializer(Array.class, valueKey -> new ArraySerializer<>(valueKey.getDefaultValue()));
		this.addSerializer(Table.class, valueKey -> new TableSerializer<>(valueKey.getDefaultValue()));
	}

	@Override
	public @NotNull String getExtension() {
		return "json";
	}

	@Override
	public @Nullable SemanticVersion getVersion(InputStream inputStream) throws IOException, VersionParsingException {
		String s = this.getRepresentation(inputStream).get("version").getAsString();
		return s == null ? null : SemanticVersion.parse(s);
	}

	@Override
	public @NotNull JsonObject getRepresentation(InputStream inputStream) throws IOException {
		Reader reader = new InputStreamReader(inputStream);
		JsonObject object = new JsonParser().parse(reader).getAsJsonObject();
		reader.close();

		return object;
	}

	@Override
	protected JsonObject start(@Nullable Iterable<String> comments) {
		return new JsonObject();
	}

	@Override
	protected <R extends JsonElement> R add(JsonObject object, String key, R representation, Iterable<String> comments) {
		object.add(key, representation);
		return representation;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <V> V get(JsonObject object, String s) {
		return (V) object.get(s);
	}

	@Override
	protected void write(JsonObject root, Writer writer, boolean minimal) throws IOException {
		this.gson.toJson(root, writer);
		writer.flush();
		writer.close();
	}

	interface GsonValueSerializer<R extends JsonElement, V> extends ValueSerializer<JsonElement, R, V> {
	}

	private static class BooleanSerializer implements GsonValueSerializer<JsonPrimitive, Boolean> {
		static BooleanSerializer INSTANCE = new BooleanSerializer();

		@Override
		public JsonPrimitive serialize(Boolean value) {
			return new JsonPrimitive(value);
		}

		@Override
		public Boolean deserialize(JsonElement representation) {
			return representation.getAsBoolean();
		}
	}

	private static class IntSerializer implements GsonValueSerializer<JsonPrimitive, Integer> {
		static IntSerializer INSTANCE = new IntSerializer();

		@Override
		public JsonPrimitive serialize(Integer value) {
			return new JsonPrimitive(value);
		}

		@Override
		public Integer deserialize(JsonElement representation) {
			return representation.getAsInt();
		}
	}

	private static class LongSerializer implements GsonValueSerializer<JsonPrimitive, Long> {
		static LongSerializer INSTANCE = new LongSerializer();

		@Override
		public JsonPrimitive serialize(Long value) {
			return new JsonPrimitive(value);
		}

		@Override
		public Long deserialize(JsonElement representation) {
			return representation.getAsLong();
		}
	}

	private static class StringSerializer implements GsonValueSerializer<JsonPrimitive, String> {
		static StringSerializer INSTANCE = new StringSerializer();

		@Override
		public JsonPrimitive serialize(String value) {
			return new JsonPrimitive(value);
		}

		@Override
		public String deserialize(JsonElement representation) {
			return representation.getAsString();
		}
	}

	private static class FloatSerializer implements GsonValueSerializer<JsonPrimitive, Float> {
		public static final FloatSerializer INSTANCE = new FloatSerializer();

		@Override
		public JsonPrimitive serialize(Float value) {
			return new JsonPrimitive(value);
		}

		@Override
		public Float deserialize(JsonElement representation) {
			return representation.getAsFloat();
		}
	}

	private static class DoubleSerializer implements GsonValueSerializer<JsonPrimitive, Double> {
		public static final DoubleSerializer INSTANCE = new DoubleSerializer();

		@Override
		public JsonPrimitive serialize(Double value) {
			return new JsonPrimitive(value);
		}

		@Override
		public Double deserialize(JsonElement representation) {
			return representation.getAsDouble();
		}
	}

	private class ArraySerializer<T> implements GsonValueSerializer<JsonArray, Array<T>> {
		private final Array<T> defaultValue;

		private ArraySerializer(Array<T> defaultValue) {
			this.defaultValue = defaultValue;
		}

		@Override
		public JsonArray serialize(Array<T> value) {
			JsonArray array = new JsonArray();
			ValueSerializer<? extends JsonElement, ?, T> serializer = GsonSerializer.this.getSerializer(value.getValueClass());

			for (T t : value) {
				array.add(serializer.serialize(t));
			}

			return array;
		}

		@Override
		public Array<T> deserialize(JsonElement representation) {
			ValueSerializer<JsonElement, ?, T> serializer = GsonSerializer.this.getSerializer(this.defaultValue.getValueClass());

			JsonArray array = (JsonArray) representation;

			//noinspection unchecked
			T[] values = (T[]) java.lang.reflect.Array.newInstance(defaultValue.getValueClass(), array.size());

			int i = 0;

			for (JsonElement element : array) {
				values[i++] = serializer.deserialize(element);
			}

			return new Array<>(this.defaultValue.getValueClass(), this.defaultValue.getDefaultValue(), values);
		}
	}

	private class TableSerializer<T> implements GsonValueSerializer<JsonObject, Table<T>> {
		private final Table<T> defaultValue;

		private TableSerializer(Table<T> defaultValue) {
			this.defaultValue = defaultValue;
		}

		@Override
		public JsonObject serialize(Table<T> table) {
			JsonObject object = new JsonObject();
			ValueSerializer<? extends JsonElement, ?, T> serializer = GsonSerializer.this.getSerializer(this.defaultValue.getValueClass());

			for (Table.Entry<String, T> t : table) {
				object.add(t.getKey(), serializer.serialize(t.getValue()));
			}

			return object;
		}

		@Override
		public Table<T> deserialize(JsonElement representation) {
			ValueSerializer<JsonElement, ?, T> serializer = GsonSerializer.this.getSerializer(this.defaultValue.getValueClass());

			JsonObject object = (JsonObject) representation;

			//noinspection unchecked
			Table.Entry<String, T>[] values = (Table.Entry<String, T>[]) java.lang.reflect.Array.newInstance(Table.Entry.class, object.size());

			int i = 0;

			for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
				values[i++] = new Table.Entry<>(entry.getKey(), serializer.deserialize(entry.getValue()));
			}

			return new Table<>(this.defaultValue.getValueClass(), this.defaultValue.getDefaultValue(), values);
		}
	}
}
