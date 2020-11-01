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

package net.fabricmc.fabric.mixin.biome.modification;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.BiomeParticleConfig;

@Mixin(BiomeEffects.class)
public interface BiomeEffectsAccessor {
	@Accessor("fogColor")
	@Mutable
	void fabric_setFogColor(int value);

	@Accessor("waterColor")
	@Mutable
	void fabric_setWaterColor(int value);

	@Accessor("waterFogColor")
	@Mutable
	void fabric_setWaterFogColor(int value);

	@Accessor("skyColor")
	@Mutable
	void fabric_setSkyColor(int value);

	@Accessor("foliageColor")
	@Mutable
	void fabric_setFoliageColor(Optional<Integer> value);

	@Accessor("grassColor")
	@Mutable
	void fabric_setGrassColor(Optional<Integer> value);

	@Accessor("grassColorModifier")
	@Mutable
	void fabric_setGrassColorModifier(BiomeEffects.GrassColorModifier value);

	@Accessor("particleConfig")
	@Mutable
	void fabric_setParticleConfig(Optional<BiomeParticleConfig> value);

	@Accessor("loopSound")
	@Mutable
	void fabric_setLoopSound(Optional<SoundEvent> value);

	@Accessor("moodSound")
	@Mutable
	void fabric_setMoodSound(Optional<BiomeMoodSound> value);

	@Accessor("additionsSound")
	@Mutable
	void fabric_setAdditionsSound(Optional<BiomeAdditionsSound> value);

	@Accessor("music")
	@Mutable
	void fabric_setMusic(Optional<MusicSound> value);
}
