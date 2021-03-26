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

package net.fabricmc.fabric.api.conditionalresource.v1;

import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.impl.conditionalresource.ResourceConditionsImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.util.version.VersionPredicateParser;

public final class ResourceConditions {
	private ResourceConditions() {
	}

	/**
	 * The registry for {@link ResourceCondition}s.
	 */
	public static final Registry<ResourceCondition> RESOURCE_CONDITION_REGISTRY = FabricRegistryBuilder.createSimple(ResourceCondition.class, new Identifier("fabric-conditional-resource-api-v1", "conditions")).attribute(RegistryAttribute.SYNCED).buildAndRegister();

	/**
	 * An impossible {@link ResourceCondition}.
	 */
	public static final ResourceCondition IMPOSSIBLE = (fabricMetaId, element) -> false;

	/**
	 * An always true {@link ResourceCondition}.
	 */
	public static final ResourceCondition ALWAYS = (fabricMetaId, element) -> true;

	/**
	 * A {@link ResourceCondition} specified with a boolean.
	 */
	public static final ResourceCondition BOOLEAN = (fabricMetaId, element) -> element.getAsBoolean();

	/**
	 * A {@link ResourceCondition} that ands two or more resource conditions.
	 */
	public static final ResourceCondition AND = (fabricMetaId, element) -> {
		JsonArray conditions = element.getAsJsonArray();

		if (conditions.size() == 0) {
			throw new IllegalArgumentException("Json array conditions for \"fabric:and\" is empty!");
		}

		for (JsonElement condition : conditions) {
			if (!ResourceConditions.evaluate(fabricMetaId, condition.getAsJsonObject())) {
				return false;
			}
		}

		return true;
	};

	/**
	 * A {@link ResourceCondition} that ors two or more resource conditions.
	 */
	public static final ResourceCondition OR = (fabricMetaId, element) -> {
		JsonArray conditions = element.getAsJsonArray();

		if (conditions.size() == 0) {
			throw new IllegalArgumentException("Json array conditions for \"fabric:or\" is empty!");
		}

		for (JsonElement condition : conditions) {
			if (ResourceConditions.evaluate(fabricMetaId, condition.getAsJsonObject())) {
				return true;
			}
		}

		return false;
	};

	/**
	 * A {@link ResourceCondition} that evaluates to true when a mod is loaded with semver comparison.
	 */
	public static final ResourceCondition MOD_LOADED = (fabricMetaId, element) -> {
		JsonObject object = element.getAsJsonObject();

		for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
			String modId = entry.getKey();
			Optional<Version> version = FabricLoader.getInstance().getModContainer(modId).map(ModContainer::getMetadata).map(ModMetadata::getVersion);

			if (!version.isPresent()) {
				return false;
			} else {
				JsonElement versionMatcher = entry.getValue();
				List<String> versionsToMatch;

				if (versionMatcher.isJsonPrimitive()) {
					versionsToMatch = Collections.singletonList(versionMatcher.getAsString());
				} else if (versionMatcher.isJsonArray()) {
					versionsToMatch = new ArrayList<>();

					for (JsonElement jsonElement : versionMatcher.getAsJsonArray()) {
						versionsToMatch.add(jsonElement.getAsString());
					}
				} else {
					throw new RuntimeException("Dependency version range must be a string or string array!");
				}

				boolean matched = false;

				for (String match : versionsToMatch) {
					try {
						if (VersionPredicateParser.matches(version.get(), match)) {
							matched = true;
							break;
						}
					} catch (VersionParsingException e) {
						e.printStackTrace();
						return false;
					}
				}

				if (!matched) {
					return false;
				}
			}
		}

		return true;
	};

	/**
	 * Evaluates the resource condition.
	 *
	 * @param fabricMetaId the identifier of the fabric meta
	 * @param element      the content of the condition
	 * @return whether the condition is true
	 */
	public static boolean evaluate(Identifier fabricMetaId, JsonElement element) {
		return ResourceConditionsImpl.evaluate(fabricMetaId, element);
	}
}
