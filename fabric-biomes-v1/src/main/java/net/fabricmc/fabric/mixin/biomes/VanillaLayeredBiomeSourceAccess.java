package net.fabricmc.fabric.mixin.biomes;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(VanillaLayeredBiomeSource.class)
public interface VanillaLayeredBiomeSourceAccess {
	@Accessor
	static Set<Biome> getBiomes() {
		throw new AssertionError("Mixin dummy");
	}
}
