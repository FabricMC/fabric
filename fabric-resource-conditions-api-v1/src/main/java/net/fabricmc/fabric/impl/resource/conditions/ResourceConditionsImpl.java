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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.fabricmc.fabric.api.resource.conditions.v1.DefaultResourceConditionTypes;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;

import net.minecraft.registry.tag.TagManagerLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import net.fabricmc.loader.api.FabricLoader;

public final class ResourceConditionsImpl {
	public static final Logger LOGGER = LoggerFactory.getLogger("Fabric Resource Conditions");
	public static final ThreadLocal<Map<RegistryKey<?>, Map<Identifier, Collection<RegistryEntry<?>>>>> LOADED_TAGS = new ThreadLocal<>();
	public static final ThreadLocal<FeatureSet> CURRENT_FEATURES = new ThreadLocal<>();
	public static final ThreadLocal<DynamicRegistryManager.Immutable> CURRENT_REGISTRIES = new ThreadLocal<>();

	// Condition implementations

	public static boolean conditionsMet(List<ResourceCondition> conditions, boolean and) {
		for (ResourceCondition condition : conditions) {
			if (condition.test() != and) {
				return !and;
			}
		}
		return and;
	}

	public static boolean modsLoaded(List<String> modIds, boolean and) {
		for (String modId : modIds) {
			if (FabricLoader.getInstance().isModLoaded(modId) != and) {
				return !and;
			}
		}
		return and;
	}

	public static boolean tagsPopulated(Identifier registryId, List<Identifier> tags) {
		RegistryKey<? extends Registry<?>> registryKey = RegistryKey.ofRegistry(registryId);
		Map<RegistryKey<?>, Map<Identifier, Collection<RegistryEntry<?>>>> allTags = LOADED_TAGS.get();

		if (allTags == null) {
			LOGGER.warn("Can't retrieve deserialized tags, failing tags_populated resource condition check");
			return false;
		}

		Map<Identifier, Collection<RegistryEntry<?>>> registryTags = allTags.get(registryKey);

		if (registryTags == null) {
			// No tag for this registry
			return tags.isEmpty();
		}

		for (Identifier tagId : tags) {
			Collection<RegistryEntry<?>> tag = registryTags.get(tagId);

			if (tag == null || tag.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	public static boolean featuresEnabled(List<Identifier> features) {
		FeatureSet set = FeatureFlags.FEATURE_MANAGER.featureSetOf(features, (id) -> {
			throw new IllegalStateException("Unknown feature flag: " + id);
		});

		FeatureSet currentFeatures = CURRENT_FEATURES.get();

		if (currentFeatures == null) {
			LOGGER.warn("Can't retrieve current features, failing features_enabled resource condition check.");
			return false;
		}

		return set.isSubsetOf(currentFeatures);
	}

	public static boolean registryContains(Identifier registryId, List<Identifier> entries) {
		RegistryKey<? extends Registry<?>> registryKey = RegistryKey.ofRegistry(registryId);
		DynamicRegistryManager.Immutable registries = CURRENT_REGISTRIES.get();

		if (registries == null) {
			LOGGER.warn("Can't retrieve deserialized tags, failing tags_populated resource condition check");
			return false;
		}

		Registry<?> registry = registries.get(registryKey);

		if (registry == null) {
			// No such registry
			return entries.isEmpty();
		}

		for (Identifier entryId : entries) {
			if (!registry.containsId(entryId)) {
				return false;
			}
		}

		return true;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void setTags(List<TagManagerLoader.RegistryTags<?>> tags) {
		Map<RegistryKey<?>, Map<Identifier, Collection<RegistryEntry<?>>>> tagMap = new HashMap<>();

		for (TagManagerLoader.RegistryTags<?> registryTags : tags) {
			tagMap.put(registryTags.key(), (Map) registryTags.tags());
		}

		LOADED_TAGS.set(tagMap);
	}


	static {
		DefaultResourceConditionTypes.init();
	}
}
