package net.fabricmc.fabric.mixin.biome;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.BiomeParticleConfig;

@Mixin(BiomeEffects.class)
public interface BiomeEffectsAccessor {
	@Accessor
	int getFogColor();

	@Accessor
	int getWaterColor();

	@Accessor
	int getWaterFogColor();

	@Accessor
	int getSkyColor();

	@Accessor
	Optional<Integer> getFoliageColor();

	@Accessor
	Optional<Integer> getGrassColor();

	@Accessor
	BiomeEffects.GrassColorModifier getGrassColorModifier();

	@Accessor
	Optional<BiomeParticleConfig> getParticleConfig();

	@Accessor
	Optional<SoundEvent> getLoopSound();

	@Accessor
	Optional<BiomeMoodSound> getMoodSound();

	@Accessor
	Optional<BiomeAdditionsSound> getAdditionsSound();

	@Accessor
	Optional<MusicSound> getMusic();
}

