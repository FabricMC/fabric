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

package net.fabricmc.fabric.mixin.registry.sync;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.fabricmc.fabric.impl.registry.sync.DynamicRegistriesImpl;
import net.fabricmc.fabric.impl.registry.sync.DynamicRegistryViewImpl;

@Mixin(RegistryLoader.class)
public class RegistryLoaderMixin {
	@Unique
	private static final ThreadLocal<Boolean> IS_SERVER = ThreadLocal.withInitial(() -> false);

	/**
	 * Sets IS_SERVER flag. Note that this must be reset after call, as the render thread
	 * invokes this method as well.
	 */
	@WrapOperation(method = "method_56515", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/RegistryLoader;load(Lnet/minecraft/registry/RegistryLoader$RegistryLoadable;Lnet/minecraft/registry/DynamicRegistryManager;Ljava/util/List;)Lnet/minecraft/registry/DynamicRegistryManager$Immutable;"))
	private static DynamicRegistryManager.Immutable wrapIsServerCall(@Coerce Object registryLoadable, DynamicRegistryManager baseRegistryManager, List<RegistryLoader.Entry<?>> entries, Operation<DynamicRegistryManager.Immutable> original) {
		try {
			IS_SERVER.set(true);
			return original.call(registryLoadable, baseRegistryManager, entries);
		} finally {
			IS_SERVER.set(false);
		}
	}

	@Inject(
			method = "load(Lnet/minecraft/registry/RegistryLoader$RegistryLoadable;Lnet/minecraft/registry/DynamicRegistryManager;Ljava/util/List;)Lnet/minecraft/registry/DynamicRegistryManager$Immutable;",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V",
					ordinal = 0
			)
	)
	private static void beforeLoad(@Coerce Object registryLoadable, DynamicRegistryManager baseRegistryManager, List<RegistryLoader.Entry<?>> entries, CallbackInfoReturnable<DynamicRegistryManager.Immutable> cir, @Local(ordinal = 1) List<RegistryLoader.class_9158<?>> registriesList) {
		if (!IS_SERVER.get()) return;

		Map<RegistryKey<? extends Registry<?>>, Registry<?>> registries = new IdentityHashMap<>(registriesList.size());

		for (RegistryLoader.class_9158<?> entry : registriesList) {
			registries.put(entry.registry().getKey(), entry.registry());
		}

		DynamicRegistrySetupCallback.EVENT.invoker().onRegistrySetup(new DynamicRegistryViewImpl(registries));
	}

	// Vanilla doesn't mark namespaces in the directories of dynamic registries at all,
	// so we prepend the directories with the namespace if it's a modded registry registered using the Fabric API.
	@Inject(method = "getPath", at = @At("RETURN"), cancellable = true)
	private static void prependDirectoryWithNamespace(Identifier id, CallbackInfoReturnable<String> info) {
		if (!id.getNamespace().equals(Identifier.DEFAULT_NAMESPACE)
				&& DynamicRegistriesImpl.FABRIC_DYNAMIC_REGISTRY_KEYS.contains(RegistryKey.ofRegistry(id))) {
			final String newPath = id.getNamespace() + "/" + info.getReturnValue();
			info.setReturnValue(newPath);
		}
	}
}
