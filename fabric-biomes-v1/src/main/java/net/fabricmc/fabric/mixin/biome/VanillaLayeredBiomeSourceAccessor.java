package net.fabricmc.fabric.mixin.biome;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;

@Mixin(VanillaLayeredBiomeSource.class)
public interface VanillaLayeredBiomeSourceAccessor {
	@Accessor("BIOMES")
	static List<RegistryKey<Biome>> getBiomes() {
		throw new AssertionError("This should not happen!");
	}
}
