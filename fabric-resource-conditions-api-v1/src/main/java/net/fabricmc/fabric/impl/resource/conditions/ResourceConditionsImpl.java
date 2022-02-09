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

package net.fabricmc.fabric.impl.resource.conditions;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.class_6862;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.loader.api.FabricLoader;

@ApiStatus.Internal
public class ResourceConditionsImpl {
	public static final Logger LOGGER = LoggerFactory.getLogger("Fabric Resource Conditions");

	// Providers

	public static ConditionJsonProvider array(Identifier id, ConditionJsonProvider... values) {
		Preconditions.checkArgument(values.length > 0, "Must register at least one value.");

		return new ConditionJsonProvider() {
			@Override
			public Identifier getConditionId() {
				return id;
			}

			@Override
			public void writeParameters(JsonObject object) {
				JsonArray array = new JsonArray();

				for (ConditionJsonProvider provider : values) {
					array.add(provider.toJson());
				}

				object.add("values", array);
			}
		};
	}

	public static ConditionJsonProvider mods(Identifier id, String... modIds) {
		Preconditions.checkArgument(modIds.length > 0, "Must register at least one mod id.");

		return new ConditionJsonProvider() {
			@Override
			public Identifier getConditionId() {
				return id;
			}

			@Override
			public void writeParameters(JsonObject object) {
				JsonArray array = new JsonArray();

				for (String modId : modIds) {
					array.add(modId);
				}

				object.add("values", array);
			}
		};
	}

	public static <T> ConditionJsonProvider tagsPopulated(Identifier id, class_6862<T>... tags) {
		Preconditions.checkArgument(tags.length > 0, "Must register at least one tag.");

		return new ConditionJsonProvider() {
			@Override
			public Identifier getConditionId() {
				return id;
			}

			@Override
			public void writeParameters(JsonObject object) {
				JsonArray array = new JsonArray();

				for (class_6862<T> tag : tags) {
					array.add(tag.location().toString());
				}

				object.add("values", array);
			}
		};
	}

	// Condition implementations

	public static boolean modsLoadedMatch(JsonObject object, boolean and) {
		JsonArray array = JsonHelper.getArray(object, "values");

		for (JsonElement element : array) {
			if (element.isJsonPrimitive()) {
				if (FabricLoader.getInstance().isModLoaded(element.getAsString()) != and) {
					return !and;
				}
			} else {
				throw new JsonParseException("Invalid mod id entry: " + element);
			}
		}

		return and;
	}

	public static <T> boolean tagsPopulatedMatch(JsonObject object, RegistryKey<? extends Registry<T>> registryKey) {
		JsonArray array = JsonHelper.getArray(object, "values");

		for (JsonElement element : array) {
			if (element.isJsonPrimitive()) {
				Identifier id = new Identifier(element.getAsString());
				// TODO 22w06a check me later
				class_6862<T> tag = class_6862.method_40092(registryKey, id);
				Registry<T> registry = (Registry<T>) Registry.REGISTRIES.get(registryKey.getValue());

				if (!registry.method_40286(tag).iterator().hasNext()) {
					return false;
				}
			} else {
				throw new JsonParseException("Invalid tag id entry: " + element);
			}
		}

		return true;
	}
}
