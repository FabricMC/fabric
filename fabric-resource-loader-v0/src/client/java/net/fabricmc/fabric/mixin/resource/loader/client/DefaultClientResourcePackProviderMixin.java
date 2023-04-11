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

package net.fabricmc.fabric.mixin.resource.loader.client;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.client.resource.DefaultClientResourcePackProvider;
import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.fabric.impl.client.resource.loader.FabricWrappedVanillaResourcePack;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackUtil;

@Mixin(DefaultClientResourcePackProvider.class)
public class DefaultClientResourcePackProviderMixin {
	/**
	 * Injects into the method which registers/creates vanilla built-in resource packs,
	 * and replaces the local {@link net.minecraft.resource.ResourcePackProfile.PackFactory}
	 * instance with our custom wrapper that supports loading from mods.
	 */
	@ModifyArg(
			method = "create(Ljava/lang/String;Lnet/minecraft/resource/ResourcePackProfile$PackFactory;Lnet/minecraft/text/Text;)Lnet/minecraft/resource/ResourcePackProfile;",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/resource/ResourcePackProfile;create(Ljava/lang/String;Lnet/minecraft/text/Text;ZLnet/minecraft/resource/ResourcePackProfile$PackFactory;Lnet/minecraft/resource/ResourceType;Lnet/minecraft/resource/ResourcePackProfile$InsertionPosition;Lnet/minecraft/resource/ResourcePackSource;)Lnet/minecraft/resource/ResourcePackProfile;"
			),
			index = 3
	)
	private ResourcePackProfile.PackFactory onCreateVanillaBuiltinResourcePack(String name, Text displayName, boolean alwaysEnabled,
			ResourcePackProfile.PackFactory packFactory, ResourceType type, ResourcePackProfile.InsertionPosition position, ResourcePackSource source) {
		return factory -> new FabricWrappedVanillaResourcePack((AbstractFileResourcePack) packFactory.open(name), getModResourcePacks(name));
	}

	/**
	 * {@return all baked-in mod resource packs that provide resources in the specified subPath}.
	 */
	private static List<ModResourcePack> getModResourcePacks(String subPath) {
		List<ModResourcePack> packs = new ArrayList<>();
		ModResourcePackUtil.appendModResourcePacks(packs, ResourceType.CLIENT_RESOURCES, subPath);
		return packs;
	}
}
