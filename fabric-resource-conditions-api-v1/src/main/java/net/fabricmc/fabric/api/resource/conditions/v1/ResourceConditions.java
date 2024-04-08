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

package net.fabricmc.fabric.api.resource.conditions.v1;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.resource.conditions.conditions.AllModsLoadedResourceCondition;
import net.fabricmc.fabric.impl.resource.conditions.conditions.AndResourceCondition;
import net.fabricmc.fabric.impl.resource.conditions.conditions.AnyModsLoadedResourceCondition;
import net.fabricmc.fabric.impl.resource.conditions.conditions.FeaturesEnabledResourceCondition;
import net.fabricmc.fabric.impl.resource.conditions.conditions.NotResourceCondition;
import net.fabricmc.fabric.impl.resource.conditions.conditions.OrResourceCondition;
import net.fabricmc.fabric.impl.resource.conditions.conditions.RegistryContainsResourceCondition;
import net.fabricmc.fabric.impl.resource.conditions.conditions.TagsPopulatedResourceCondition;
import net.fabricmc.fabric.impl.resource.conditions.conditions.TrueResourceCondition;

/**
 * Contains default resource conditions and the condition registry.
 */
public final class ResourceConditions {
	private static final Map<Identifier, ResourceConditionType<?>> REGISTERED_CONDITIONS = new ConcurrentHashMap<>();

	/**
	 * The JSON key for resource conditions, {@value #CONDITIONS_KEY}.
	 */
	public static final String CONDITIONS_KEY = "fabric:load_conditions";

	private ResourceConditions() {
	}

	/**
	 * Registers {@code condition}.
	 * @param condition the condition to register
	 * @throws IllegalArgumentException if {@code condition} is already registered
	 */
	public static void register(ResourceConditionType<?> condition) {
		Objects.requireNonNull(condition, "Condition may not be null.");

		if (REGISTERED_CONDITIONS.put(condition.id(), condition) != null) {
			throw new IllegalArgumentException("Duplicate resource condition registered with id " + condition.id());
		}
	}

	/**
	 * @return the condition with ID {@code id}, or {@code null} if there is no such condition
	 */
	public static ResourceConditionType<?> getConditionType(Identifier id) {
		return REGISTERED_CONDITIONS.get(id);
	}

	/**
	 * A condition that always passes. Has ID {@code fabric:true}.
	 */
	public static ResourceCondition alwaysTrue() {
		return new TrueResourceCondition();
	}

	/**
	 * A condition that passes if {@code condition} does not pass. Has ID {@code fabric:not} and
	 * takes a sole field, {@code value}, which is a resource condition.
	 */
	public static ResourceCondition not(ResourceCondition condition) {
		return new NotResourceCondition(condition);
	}

	public static ResourceCondition and(ResourceCondition... conditions) {
		return new AndResourceCondition(List.of(conditions));
	}

	public static ResourceCondition or(ResourceCondition... conditions) {
		return new OrResourceCondition(List.of(conditions));
	}

	public static ResourceCondition allModsLoaded(String... modIds) {
		return new AllModsLoadedResourceCondition(List.of(modIds));
	}

	public static ResourceCondition anyModsLoaded(String... modIds) {
		return new AnyModsLoadedResourceCondition(List.of(modIds));
	}

	@SafeVarargs
	public static <T> ResourceCondition tagsPopulated(TagKey<T>... tags) {
		return new TagsPopulatedResourceCondition(tags);
	}

	@SafeVarargs
	public static <T> ResourceCondition tagsPopulated(RegistryKey<? extends Registry<T>> registry, TagKey<T>... tags) {
		return new TagsPopulatedResourceCondition(registry.getValue(), tags);
	}

	public static ResourceCondition featuresEnabled(Identifier... features) {
		return new FeaturesEnabledResourceCondition(features);
	}

	public static ResourceCondition featuresEnabled(FeatureFlag... features) {
		return new FeaturesEnabledResourceCondition(features);
	}

	@SafeVarargs
	public static <T> ResourceCondition registryContains(RegistryKey<T>... entries) {
		return new RegistryContainsResourceCondition(entries);
	}

	public static <T> ResourceCondition registryContains(RegistryKey<? extends Registry<T>> registry, Identifier... entries) {
		return new RegistryContainsResourceCondition(registry.getValue(), entries);
	}
}
