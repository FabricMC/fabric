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

import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.SerializableRegistries;

import net.fabricmc.fabric.impl.registry.sync.DynamicRegistriesImpl;

// Implements skipping empty dynamic registries with the SKIP_WHEN_EMPTY sync option.
@Mixin(SerializableRegistries.class)
abstract class SerializableRegistriesMixin {
	@Shadow
	private static Stream<DynamicRegistryManager.Entry<?>> stream(DynamicRegistryManager dynamicRegistryManager) {
		return null;
	}

	@Dynamic("method_45961: Codec.xmap in createDynamicRegistryManagerCodec")
	@Redirect(method = "method_45961", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/SerializableRegistries;stream(Lnet/minecraft/registry/DynamicRegistryManager;)Ljava/util/stream/Stream;"))
	private static Stream<DynamicRegistryManager.Entry<?>> filterNonSyncedEntries(DynamicRegistryManager drm) {
		return stream(drm).filter(entry -> {
			boolean canSkip = DynamicRegistriesImpl.SKIP_EMPTY_SYNC_REGISTRIES.contains(entry.key());
			return !canSkip || entry.value().size() > 0;
		});
	}
}
