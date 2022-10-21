package net.fabricmc.fabric.api.datagen.v1.helpers;

import java.util.List;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.provider.consumers.SoundEventConsumer;

/**
 * Mojang did not provide a codec for sounds.json entried, this is Fabric's implementation of a sounds.json entry used for data generation in {@link SoundEventConsumer}
 * Comes with a CODEC field that can be used for other purposes.
 */
public record SoundEventEntry(boolean replace, String subtitle, List<Either<String, SoundFileInfo>> sounds) {
	/**
	 * Codec for sounds.json entry.
	 * @see <a href="https://minecraft.fandom.com/wiki/Sounds.json">Minecraft Wiki - Sounds.json</a>
	 */
	public static final Codec<SoundEventEntry> CODEC = RecordCodecBuilder.create(
			instance -> {
				return instance.group(
						Codec.BOOL.orElse(false).fieldOf("replace").forGetter((entry) -> entry.replace),
						Codec.STRING.orElse("").fieldOf("subtitle").forGetter((entry) -> entry.subtitle),
						Codec.list(Codec.either(Codec.STRING, SoundFileInfo.CODEC)).fieldOf("sounds").forGetter(entry -> entry.sounds)
				).apply(instance, SoundEventEntry::new);
			}
	);

	/**
	 * Two values are available: "sound" and "event".
	 * <p>
	 * <p>
	 * "sound" causes the value of "name" in the sound file info to be interpreted as the name of a file.
	 * <p>
	 * <p>
	 * "event" causes the value of "name" in the sound file info to be interpreted as the name of an already defined event.
	 */
	public enum SoundFileNameType {
		SOUND,
		EVENT
	}

	/**
	 * A record that represents a sound file info object in sounds.json
	 *
	 * @param name                 The identifier of the sound file, excluding .ogg and the sound folder in the path.
	 * @param volume               The volume for playing this sound. Value is a decimal between 0.0 and 1.0.
	 * @param pitch                Plays the pitch at the specified value.
	 * @param weight               The chance that this sound is selected to play when this sound event is triggered.
	 * @param stream               True if this sound should be streamed from its file. It is recommended that this is set to "true" for sounds that have a duration longer than a few seconds to avoid lag.
	 * @param attenuation_distance Modify sound reduction rate based on distance. Used by portals, beacons, and conduits.
	 * @param preload              True if this sound should be loaded when loading the pack instead of when the sound is played.
	 * @param type                 See {@link SoundFileNameType} for more information.
	 * @see <a href="https://minecraft.fandom.com/wiki/Sounds.json">Minecraft Wiki - Sounds.json</a>
	 */
	public record SoundFileInfo(Identifier name, float volume, float pitch, int weight, boolean stream,
								int attenuation_distance, boolean preload, SoundEventEntry.SoundFileNameType type) {
		/**
		 * Codec for sound file information.
		 *
		 * @see <a href="https://minecraft.fandom.com/wiki/Sounds.json">Minecraft Wiki - Sounds.json</a>
		 */
		public static final Codec<SoundFileInfo> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
						Identifier.CODEC.fieldOf("name").forGetter(entry -> entry.name),
						Codec.FLOAT.orElse(1f).fieldOf("volume").forGetter(entry -> entry.volume),
						Codec.FLOAT.orElse(1f).fieldOf("pitch").forGetter(entry -> entry.pitch),
						Codec.INT.orElse(1).fieldOf("weight").forGetter(entry -> entry.weight),
						Codec.BOOL.orElse(false).fieldOf("stream").forGetter(entry -> entry.stream),
						Codec.INT.orElse(16).fieldOf("attenuation_distance").forGetter(entry -> entry.attenuation_distance),
						Codec.BOOL.orElse(false).fieldOf("preload").forGetter(entry -> entry.preload),
						Codec.STRING.orElse("sound").fieldOf("type").forGetter(entry -> entry.type.name())
				).apply(instance, (s, aFloat, aFloat2, integer, aBoolean, integer2, aBoolean2, s2) -> new SoundFileInfo(s, aFloat, aFloat2, integer, aBoolean, integer2, aBoolean2, SoundFileNameType.valueOf(s2)))
		);
	}
}
