package net.fabricmc.fabric.mixin.biome;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.biome.SpawnSettings;

@Mixin(SpawnSettings.SpawnDensity.class)
public interface SpawnDensityAccessor {
	@Invoker("<init>")
	static SpawnSettings.SpawnDensity create(double gravityLimit, double mass) {
		throw new AbstractMethodError();
	}
}
