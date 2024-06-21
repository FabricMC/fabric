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

package net.fabricmc.fabric.test.registry.sync.mixin;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.collection.IndexedIterable;

import net.fabricmc.fabric.test.registry.sync.RegistrySyncTest;

@Mixin(PacketCodecs.class)
public interface PacketCodecsMixin {
	@Inject(method = "registry", at = @At("HEAD"))
	private static <T, R> void checkSynced(RegistryKey<? extends Registry<T>> registry, Function<Registry<T>, IndexedIterable<R>> registryTransformer, CallbackInfoReturnable<PacketCodec<RegistryByteBuf, R>> cir) {
		RegistrySyncTest.checkSyncedRegistry(registry);
	}
}
