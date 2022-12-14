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

package net.fabricmc.fabric.api.datagen.v1.sound;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraft.util.Identifier;

/**
 * Holds data for an individual sound's JSON entry to be generated and added for some {@link net.minecraft.sound.SoundEvent}.
 */
public record SoundEntry(Identifier name, float volume, float pitch, int weight, boolean stream, int attenuationDistance, boolean preload, boolean event) {
	private boolean allDefaults() {
		return volume == 1 && pitch == 1 && weight == 1 && !stream && attenuationDistance == 16 && !preload && !event;
	}

	public JsonElement toJson() {
		if (allDefaults()) {
			return new JsonPrimitive(name.toString());
		} else {
			JsonObject soundEntry = new JsonObject();
			soundEntry.addProperty("name", name.toString());

			if (volume != 1) {
				soundEntry.addProperty("volume", volume);
			}

			if (pitch != 1) {
				soundEntry.addProperty("pitch", pitch);
			}

			if (weight != 1) {
				soundEntry.addProperty("weight", weight);
			}

			if (stream) {
				soundEntry.addProperty("stream", true);
			}

			if (attenuationDistance != 16) {
				soundEntry.addProperty("attenuation_distance", attenuationDistance);
			}

			if (preload) {
				soundEntry.addProperty("preload", true);
			}

			if (event) {
				soundEntry.addProperty("type", "event");
			}

			return soundEntry;
		}
	}
}
