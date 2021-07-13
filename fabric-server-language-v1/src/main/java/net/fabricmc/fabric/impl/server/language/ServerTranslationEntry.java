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

package net.fabricmc.fabric.impl.server.language;

import java.util.Set;
import java.util.stream.Collectors;

import net.fabricmc.loader.api.metadata.ModDependency;
import net.fabricmc.loader.api.metadata.ModMetadata;

/**
 * A utility class for holding translation mappings prior collision resolution.
 */
public class ServerTranslationEntry {
	private ModMetadata providingModMetadata;
	private String key;
	private String value;

	public ServerTranslationEntry(ModMetadata providingModMetadata, String key, String value) {
		this.providingModMetadata = providingModMetadata;
		this.key = key;
		this.value = value;
	}

	public String getModId() {
		return providingModMetadata.getId();
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public Set<String> getDependencyIds() {
		Set<String> deps = providingModMetadata.getDepends().stream().map(ModDependency::getModId).collect(Collectors.toSet());
		// For the purposes of handling key collisions, all mods should depend on minecraft
		if (!getModId().equals("minecraft")) deps.add("minecraft");
		return deps;
	}

	public int getDependencyIntersectionSize(Set<String> idSet) {
		Set<String> intersection = getDependencyIds();
		intersection.retainAll(idSet);
		return intersection.size();
	}
}
