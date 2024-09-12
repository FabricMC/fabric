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

package net.fabricmc.fabric.mixin.item;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.Decoder;

import net.fabricmc.fabric.api.item.v1.EnchantmentSource;
import net.fabricmc.fabric.impl.item.EnchantmentUtil;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.resource.Resource;

import net.minecraft.text.Text;
import net.minecraft.util.Util;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.component.ComponentMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryInfo;

import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;

import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RegistryLoader.class)
abstract class RegistryLoaderMixin {
	@Unique
	private static final ThreadLocal<EnchantmentSource> FABRIC_API$SOURCE = ThreadLocal.withInitial(() -> EnchantmentSource.DATA_PACK);

	@Inject(
			method = "parseAndAdd",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/registry/MutableRegistry;add(Lnet/minecraft/registry/RegistryKey;Ljava/lang/Object;Lnet/minecraft/registry/entry/RegistryEntryInfo;)Lnet/minecraft/registry/entry/RegistryEntry$Reference;",
					shift = At.Shift.BEFORE
			)
	)
	private static <E> void determineSource(
			MutableRegistry<E> registry,
			Decoder<E> decoder,
			RegistryOps<JsonElement> ops,
			RegistryKey<E> key,
			Resource resource,
			RegistryEntryInfo entryInfo,
			CallbackInfo ci
	) {
		if (key.isOf(RegistryKeys.ENCHANTMENT)) {
			FABRIC_API$SOURCE.set(EnchantmentUtil.determineSource(resource));
		}
	}

	@WrapOperation(
			method = "parseAndAdd",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/registry/MutableRegistry;add(Lnet/minecraft/registry/RegistryKey;Ljava/lang/Object;Lnet/minecraft/registry/entry/RegistryEntryInfo;)Lnet/minecraft/registry/entry/RegistryEntry$Reference;"
			)
	)
	@SuppressWarnings("unchecked")
	private static <T> RegistryEntry.Reference<T> afterParse(
			MutableRegistry<T> instance,
			RegistryKey<T> registryKey,
			Object object,
			RegistryEntryInfo registryEntryInfo,
			Operation<RegistryEntry.Reference<T>> original
	) {
		if (object instanceof Enchantment enchantment) {
			object = EnchantmentUtil.modify((RegistryKey<Enchantment>)registryKey, enchantment, FABRIC_API$SOURCE.get());
			FABRIC_API$SOURCE.remove();
		}

		return original.call(instance, registryKey, object, registryEntryInfo);
	}
}
