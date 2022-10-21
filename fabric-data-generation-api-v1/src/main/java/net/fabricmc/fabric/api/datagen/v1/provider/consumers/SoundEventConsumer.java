package net.fabricmc.fabric.api.datagen.v1.provider.consumers;


import com.mojang.datafixers.util.Either;

import net.fabricmc.fabric.api.datagen.v1.helpers.SoundEventEntry;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public interface SoundEventConsumer {
	/**
	 * Add a {@link SoundEventEntry} to the sounds.json file.
	 * @param event The sound event entry.
	 * @param soundKey The sound event key - eg: entity.enderman.death
	 */
	void add(String soundKey, SoundEventEntry event);

	/**
	 * Add a {@link SoundEvent} to the sounds.json file.
	 * @param event The sound event to add.
	 * @param sound The sound file in the /sounds folder, in <code>namespace:filename</code> format
	 */
	default void add(SoundEvent event, Identifier sound) {
		add(event, false, sound);
	}

	/**
	 * Add a {@link SoundEvent} to the sounds.json file.
	 * @param event The sound event to add.
	 * @param replace Should the sound event replace pre-existing ones?
	 * @param sound The sound file in the /sounds folder, in <code>namespace:filename</code> format
	 */
	default void add(SoundEvent event, boolean replace, Identifier sound) {
		add(event, replace, "", sound);
	}

	/**
	 * Add a {@link SoundEvent} to the sounds.json file.
	 * @param event The sound event to add.
	 * @param replace Should the sound event replace pre-existing ones?
	 * @param subtitle The translation key of the subtitle to show when this sound is played.
	 * @param sound The sound file in the /sounds folder, in <code>namespace:filename</code> format
	 */
	default void add(SoundEvent event, boolean replace, String subtitle, Identifier sound) {
		ArrayList<Either<String, SoundEventEntry.SoundFileInfo>> eitherList = new ArrayList<Either<String, SoundEventEntry.SoundFileInfo>>();
		eitherList.add(Either.left(sound.toString()));
		add(event.getId().getPath(), new SoundEventEntry(replace, subtitle, eitherList));
	}
}
