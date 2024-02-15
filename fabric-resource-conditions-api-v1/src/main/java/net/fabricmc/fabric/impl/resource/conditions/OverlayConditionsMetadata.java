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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;

import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.util.JsonHelper;

public record OverlayConditionsMetadata(JsonArray overlays) {
	private static final Pattern DIRECTORY_NAME_PATTERN = Pattern.compile("[-_a-zA-Z0-9.]+");
	public static final ResourceMetadataSerializer<OverlayConditionsMetadata> SERIALIZER = new ResourceMetadataSerializer<>() {
		@Override
		public JsonObject toJson(OverlayConditionsMetadata metadata) {
			JsonObject object = new JsonObject();
			object.add(ResourceConditions.ENTRIES_KEY, metadata.overlays());
			return object;
		}

		@Override
		public String getKey() {
			return ResourceConditions.OVERLAYS_KEY;
		}

		@Override
		public OverlayConditionsMetadata fromJson(JsonObject json) {
			return new OverlayConditionsMetadata(json.getAsJsonArray(ResourceConditions.ENTRIES_KEY));
		}
	};

	private static boolean validDirectoryName(String directoryName) {
		return DIRECTORY_NAME_PATTERN.matcher(directoryName).matches();
	}

	public List<String> getAppliedOverlays() {
		List<String> appliedOverlays = new ArrayList<>();

		for (JsonElement element : this.overlays()) {
			if (element.isJsonObject()) {
				JsonObject object = element.getAsJsonObject();
				String directoryName = JsonHelper.getString(object, "directory");

				if (validDirectoryName(directoryName)) {
					if (ResourceConditions.conditionMatches(object)) {
						appliedOverlays.add(directoryName);
					}
				} else {
					throw new JsonParseException(directoryName + " is not an accepted directory name");
				}
			} else {
				throw new JsonParseException("Invalid condition entry: " + element);
			}
		}

		return appliedOverlays;
	}
}
