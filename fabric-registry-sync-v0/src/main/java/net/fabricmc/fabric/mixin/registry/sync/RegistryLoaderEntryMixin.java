package net.fabricmc.fabric.mixin.registry.sync;

import com.mojang.serialization.Lifecycle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.SimpleRegistry;

import net.fabricmc.fabric.impl.registry.sync.DynamicRegistriesImpl;

@Mixin(RegistryLoader.Entry.class)
abstract class RegistryLoaderEntryMixin<T> {
	@Redirect(method = "getLoader", at = @At(value = "NEW", target = "(Lnet/minecraft/registry/RegistryKey;Lcom/mojang/serialization/Lifecycle;)Lnet/minecraft/registry/SimpleRegistry;"))
	private SimpleRegistry<T> redirectDynamicRegistryCreation(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle) {
		return DynamicRegistriesImpl.createDynamicRegistry(key, lifecycle);
	}
}
