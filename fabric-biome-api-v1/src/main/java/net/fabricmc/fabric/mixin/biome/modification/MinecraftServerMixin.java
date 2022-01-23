package net.fabricmc.fabric.mixin.biome.modification;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.level.LevelProperties;

import net.fabricmc.fabric.impl.biome.modification.BiomeModificationImpl;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
	@Shadow
	private DynamicRegistryManager.Impl registryManager;

	@Shadow
	private SaveProperties saveProperties;

	@Inject(method = "<init>", at = @At(value = "RETURN"))
	private void finalizeWorldGen(CallbackInfo ci) {
		if (!(saveProperties instanceof LevelProperties levelProperties)) {
			throw new RuntimeException("Incompatible SaveProperties passed to MinecraftServer: " + saveProperties);
		}

		BiomeModificationImpl.INSTANCE.finalizeWorldGen(registryManager, levelProperties);
	}
}
