package net.fabricmc.fabric.api.datagen.v1.sound;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Identifier;

public record SoundEntryBuilder(Identifier name, float volume, float pitch, int weight, boolean stream, int attenuationDistance, boolean preload, @Nullable Type type) {
	// TODO: add more constructors for different param combinations
	public SoundEntryBuilder(Identifier name, Type type) {
		this(name, 1, 1, 1, false, 16, false, type);
	}

	public SoundEntryBuilder(Identifier name) {
		this(name, 1, 1, 1, false, 16, false, null);
	}

	private boolean allDefaults() {
		return volume == 1 && pitch == 1 && weight == 1 && !stream && attenuationDistance == 16 && !preload && type != null;
	}

	public JsonElement build() {
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

			if (type != null) {
				soundEntry.addProperty("type", type.name().toLowerCase());
			}

			return soundEntry;
		}
	}

	public enum Type {
		SOUND,
		EVENT
	}
}
