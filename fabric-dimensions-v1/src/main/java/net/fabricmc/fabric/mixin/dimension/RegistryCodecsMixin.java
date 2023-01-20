package net.fabricmc.fabric.mixin.dimension;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;

import net.fabricmc.fabric.impl.dimension.FailSoftMapCodec;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryCodecs;

import net.minecraft.registry.RegistryKey;

import net.minecraft.registry.SimpleRegistry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RegistryCodecs.class)
public class RegistryCodecsMixin {
	/**
	 * Fix the issue that cannot load world after uninstalling a dimension mod/datapack.
	 * After uninstalling a dimension mod/datapack, the dimension config in `level.dat` file cannot be deserialized.
	 * The solution is to make it fail-soft.
	 * This contains vanilla code copy and should be checked when upgrading to newer MC version.
	 * It doesn't redirect `Codec.unboundedMap` because `FailSoftMapCodec` does not inherit `UnboundedMapCodec`.
	 * Currently, `createKeyedRegistryCodec` is only used in dimension codec.
	 */
	@SuppressWarnings("UnstableApiUsage")
	@Inject(
			method = "createKeyedRegistryCodec",
			at = @At("HEAD"),
			cancellable = true
	)
	private static <E> void injectCreateKeyedRegistryCodec(
			RegistryKey<? extends Registry<E>> registryRef, Lifecycle lifecycle,
			Codec<E> elementCodec, CallbackInfoReturnable<Codec<Registry<E>>> cir
	) {
		FailSoftMapCodec<RegistryKey<E>, E> codec =
				new FailSoftMapCodec<>(RegistryKey.createCodec(registryRef), elementCodec);
		Codec<Registry<E>> result = codec.xmap(entries -> {
			SimpleRegistry<E> mutableRegistry = new SimpleRegistry<>(registryRef, lifecycle);
			entries.forEach((key, value) -> mutableRegistry.add(key, value, lifecycle));
			return mutableRegistry.freeze();
		}, registry -> ImmutableMap.copyOf(registry.getEntrySet()));
		cir.setReturnValue(result);
	}
}
