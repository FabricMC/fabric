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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.tag.TagKey;
import net.minecraft.tag.TagManagerLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryKeys;

import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.mixin.resource.conditions.FeatureFlagAccessor;
import net.fabricmc.fabric.mixin.resource.conditions.FeatureManagerAccessor;
import net.fabricmc.loader.api.FabricLoader;

@ApiStatus.Internal
public final class ResourceConditionsImpl {
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

	@SafeVarargs
	public static <T> ConditionJsonProvider tagsPopulated(Identifier id, boolean includeRegistry, TagKey<T>... tags) {
		Preconditions.checkArgument(tags.length > 0, "Must register at least one tag.");
		final RegistryKey<? extends Registry<?>> registryRef = tags[0].registry();

		return new ConditionJsonProvider() {
			@Override
			public Identifier getConditionId() {
				return id;
			}

			@Override
			public void writeParameters(JsonObject object) {
				JsonArray array = new JsonArray();

				for (TagKey<T> tag : tags) {
					array.add(tag.id().toString());
				}

				object.add("values", array);

				if (includeRegistry && registryRef != RegistryKeys.ITEM) {
					// tags[0] is guaranteed to exist.
					// Skip if this is the default (minecraft:item)
					object.addProperty("registry", registryRef.getValue().toString());
				}
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

	/**
	 * Stores the tags deserialized by {@link TagManagerLoader} before they are bound, to use them in the tags_populated conditions.
	 * The tags are set at the end of the "apply" phase in {@link TagManagerLoader}, and cleared in {@link net.minecraft.server.DataPackContents#refresh}.
	 * If the resource reload fails, the thread local is not cleared and:
	 * - the map will remain in memory until the next reload;
	 * - any call to {@link #tagsPopulatedMatch} will check the tags from the failed reload instead of failing directly.
	 * This is probably acceptable.
	 */
	public static final ThreadLocal<Map<RegistryKey<?>, Map<Identifier, Collection<RegistryEntry<?>>>>> LOADED_TAGS = new ThreadLocal<>();

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void setTags(List<TagManagerLoader.RegistryTags<?>> tags) {
		Map<RegistryKey<?>, Map<Identifier, Collection<RegistryEntry<?>>>> tagMap = new HashMap<>();

		for (TagManagerLoader.RegistryTags<?> registryTags : tags) {
			tagMap.put(registryTags.key(), (Map) registryTags.tags());
		}

		LOADED_TAGS.set(tagMap);
	}

	public static void clearTags() {
		LOADED_TAGS.remove();
	}

	public static boolean tagsPopulatedMatch(JsonObject object) {
		String key = JsonHelper.getString(object, "registry", "minecraft:item");
		RegistryKey<? extends Registry<?>> registryRef = RegistryKey.ofRegistry(new Identifier(key));
		return tagsPopulatedMatch(object, registryRef);
	}

	public static boolean tagsPopulatedMatch(JsonObject object, RegistryKey<? extends Registry<?>> registryKey) {
		JsonArray array = JsonHelper.getArray(object, "values");
		@Nullable
		Map<RegistryKey<?>, Map<Identifier, Collection<RegistryEntry<?>>>> allTags = LOADED_TAGS.get();

		if (allTags == null) {
			LOGGER.warn("Can't retrieve deserialized tags. Failing tags_populated resource condition check.");
			return false;
		}

		Map<Identifier, Collection<RegistryEntry<?>>> registryTags = allTags.get(registryKey);

		if (registryTags == null) {
			// No tag for this registry
			return array.isEmpty();
		}

		for (JsonElement element : array) {
			if (element.isJsonPrimitive()) {
				Identifier id = new Identifier(element.getAsString());
				Collection<RegistryEntry<?>> tags = registryTags.get(id);

				if (tags == null || tags.isEmpty()) {
					return false;
				}
			} else {
				throw new JsonParseException("Invalid tag id entry: " + element);
			}
		}

		return true;
	}

	public static ConditionJsonProvider featureEnabled(Identifier id, final FeatureFlag feature) {
		final Identifier featureId = ((FeatureManagerAccessor) FeatureFlags.FEATURE_MANAGER).getFeatureFlags().entrySet()
				.stream()
				.filter(flag -> {
					FeatureFlagAccessor f1 = (FeatureFlagAccessor) flag.getValue();
					FeatureFlagAccessor f2 = (FeatureFlagAccessor) feature;
					return f2.getMask() == f2.getMask() && f1.getUniverse().toString().equals(f2.getUniverse().toString());
				})
				.findFirst()
				.map(Map.Entry::getKey)
				.orElseThrow(() -> new IllegalArgumentException("Unknown feature passed"));

		return new ConditionJsonProvider() {
			@Override
			public Identifier getConditionId() {
				return id;
			}

			@Override
			public void writeParameters(JsonObject object) {
				object.addProperty("feature", feature.toString());
			}
		};
	}

	public static FeatureSet currentFeature = FeatureFlags.DEFAULT_ENABLED_FEATURES;

	public static boolean featureEnabledMatch(JsonObject object) {
		Identifier featureId = new Identifier(JsonHelper.getString(object, "feature"));
		FeatureFlag flag = ((FeatureManagerAccessor) FeatureFlags.FEATURE_MANAGER).getFeatureFlags().get(featureId);

		if (flag == null) throw new JsonParseException("Unknown feature flag: " + featureId);

		FeatureSet features = currentFeature;
		return currentFeature.contains(flag);
	}
}
