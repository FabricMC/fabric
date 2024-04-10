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

package net.fabricmc.fabric.mixin.registry.sync.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.SerializableRegistries;

import net.fabricmc.fabric.impl.registry.sync.DynamicRegistriesImpl;

@Mixin(targets = "net/minecraft/client/network/ClientRegistries$DynamicRegistries")
public class ClientRegistriesDynamicRegistriesMixin {
	@Shadow
	@Final
	private Map<RegistryKey<? extends Registry<?>>, List<SerializableRegistries.SerializedRegistryEntry>> dynamicRegistries;

	/**
	 * Keep the pre-24w04a behavior of removing empty registries, even if the client knows that registry.
	 */
	@WrapOperation(method = "load", at = @At(value = "FIELD", target = "Lnet/minecraft/registry/RegistryLoader;SYNCED_REGISTRIES:Ljava/util/List;", opcode = Opcodes.GETSTATIC))
	private List<RegistryLoader.Entry<?>> skipEmptyRegistries(Operation<List<RegistryLoader.Entry<?>>> operation) {
		List<RegistryLoader.Entry<?>> result = new ArrayList<>(operation.call());
		result.removeIf(entry -> DynamicRegistriesImpl.SKIP_EMPTY_SYNC_REGISTRIES.contains(entry.key()) && !this.dynamicRegistries.containsKey(entry.key()));
		return result;
	}
}
