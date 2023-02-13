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
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.registry.tag.TagManagerLoader;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.loader.api.FabricLoader;

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

	public static ConditionJsonProvider featuresEnabled(Identifier id, final FeatureFlag... features) {
		final Set<Identifier> ids = new TreeSet<>(FeatureFlags.FEATURE_MANAGER.toId(FeatureFlags.FEATURE_MANAGER.featureSetOf(features)));

		return new ConditionJsonProvider() {
			@Override
			public Identifier getConditionId() {
				return id;
			}

			@Override
			public void writeParameters(JsonObject object) {
				JsonArray array = new JsonArray();

				for (Identifier id : ids) {
					array.add(id.toString());
				}

				object.add("features", array);
			}
		};
	}

	public static ConditionJsonProvider registryContains(Identifier id, Identifier registry, Identifier... entries) {
		Preconditions.checkArgument(entries.length > 0, "Must register at least one entry.");

		return new ConditionJsonProvider() {
			@Override
			public Identifier getConditionId() {
				return id;
			}

			@Override
			public void writeParameters(JsonObject object) {
				JsonArray array = new JsonArray();

				for (Identifier entry : entries) {
					array.add(entry.toString());
				}

				object.add("values", array);

				if (!RegistryKeys.ITEM.getValue().equals(registry)) {
					// Skip if this is the default (minecraft:item)
					object.addProperty("registry", registry.toString());
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

	public static final ThreadLocal<FeatureSet> CURRENT_FEATURES = ThreadLocal.withInitial(() -> FeatureFlags.DEFAULT_ENABLED_FEATURES);

	public static boolean featuresEnabledMatch(JsonObject object) {
		List<Identifier> featureIds = JsonHelper.getArray(object, "features").asList().stream().map((element) -> new Identifier(element.getAsString())).toList();
		FeatureSet set = FeatureFlags.FEATURE_MANAGER.featureSetOf(featureIds, (id) -> {
			throw new JsonParseException("Unknown feature flag: " + id);
		});

		return set.isSubsetOf(CURRENT_FEATURES.get());
	}

	public static final ThreadLocal<DynamicRegistryManager.Immutable> CURRENT_REGISTRIES = new ThreadLocal<>();

	public static boolean registryContainsMatch(JsonObject object) {
		String key = JsonHelper.getString(object, "registry", "minecraft:item");
		RegistryKey<? extends Registry<?>> registryRef = RegistryKey.ofRegistry(new Identifier(key));
		return registryContainsMatch(object, registryRef);
	}

	private static <E> boolean registryContainsMatch(JsonObject object, RegistryKey<? extends Registry<? extends E>> registryRef) {
		JsonArray array = JsonHelper.getArray(object, "values");
		DynamicRegistryManager.Immutable registries = CURRENT_REGISTRIES.get();

		if (registries == null) {
			LOGGER.warn("Can't retrieve current registries. Failing registry_contains resource condition check.");
			return false;
		}

		Optional<Registry<E>> registry = registries.getOptional(registryRef);

		if (registry.isEmpty()) {
			// No such registry
			return array.isEmpty();
		}

		for (JsonElement element : array) {
			if (element.isJsonPrimitive()) {
				Identifier id = new Identifier(element.getAsString());

				if (!registry.get().containsId(id)) {
					return false;
				}
			} else {
				throw new JsonParseException("Invalid registry entry id: " + element);
			}
		}

		return true;
	}
}
