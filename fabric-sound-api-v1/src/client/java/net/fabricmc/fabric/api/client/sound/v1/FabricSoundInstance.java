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

package net.fabricmc.fabric.api.client.sound.v1;

import java.util.concurrent.CompletableFuture;

import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundLoader;
import net.minecraft.util.Identifier;

/**
 * General purpose Fabric-provided extensions to {@link SoundInstance}.
 *
 * <p>This interface is implicitly implemented on all {@link SoundInstance}s via a mixin and interface injection.
 */
public interface FabricSoundInstance {
	/**
	 * Loads the audio stream for this sound.
	 *
	 * <p>By default this will load {@code .ogg} files from active resource packs. It may be overridden to provide a
	 * custom {@link AudioStream} implementation which provides audio from another source, such as over the network or
	 * driven from user input.
	 *
	 * @param loader          The default sound loader, capable of loading {@code .ogg} files.
	 * @param id              The resolved sound ID, equal to {@link SoundInstance#getSound()}'s location.
	 * @param repeatInstantly Whether this sound should loop. This is true when the sound
	 *                        {@linkplain SoundInstance#isRepeatable() is repeatable} and has
	 *                        {@linkplain SoundInstance#getRepeatDelay() no delay}.
	 * @return the loaded audio stream
	 */
	default CompletableFuture<AudioStream> getAudioStream(SoundLoader loader, Identifier id, boolean repeatInstantly) {
		return loader.loadStreamed(id, repeatInstantly);
	}
}
