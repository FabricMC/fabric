package net.fabricmc.fabric.mixin.registry.sync;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.registry.RegistryLoader;
import net.minecraft.server.SaveLoading;

import net.fabricmc.fabric.api.event.registry.DynamicRegistryEvents;

// Implements dynamic registry loading.
@Mixin(SaveLoading.class)
abstract class SaveLoadingMixin {
	@ModifyArg(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/SaveLoading;withRegistriesLoaded(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/registry/CombinedDynamicRegistries;Lnet/minecraft/registry/ServerDynamicRegistryType;Ljava/util/List;)Lnet/minecraft/registry/CombinedDynamicRegistries;"), allow = 1)
	private static List<RegistryLoader.Entry<?>> modifyLoadedEntries(List<RegistryLoader.Entry<?>> entries) {
		return DynamicRegistryEvents.collectDynamicRegistries();
	}
}
