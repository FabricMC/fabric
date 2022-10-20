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

package net.fabricmc.fabric.mixin.resource.loader;

import java.io.InputStream;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.resource.loader.GroupResourcePack;

/**
 * Patches getAllResources and method_41265 to work with GroupResourcePack.
 */
@Mixin(NamespaceResourceManager.class)
public class NamespaceResourceManagerMixin {
	private final ThreadLocal<List<Resource>> fabric$getAllResources$resources = new ThreadLocal<>();

	@Inject(method = "getAllResources",
			at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"),
			locals = LocalCapture.CAPTURE_FAILHARD)
	private void onGetAllResources(Identifier id, CallbackInfoReturnable<List<Resource>> cir, Identifier metadataId, List<Resource> resources) {
		this.fabric$getAllResources$resources.set(resources);
	}

	@Redirect(method = "getAllResources",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourcePack;open(Lnet/minecraft/resource/ResourceType;Lnet/minecraft/util/Identifier;)Lnet/minecraft/resource/InputSupplier;"))
	private InputSupplier<InputStream> onResourceAdd(ResourcePack pack, ResourceType type, Identifier id) {
		if (pack instanceof GroupResourcePack) {
			((GroupResourcePack) pack).appendResources(type, id, this.fabric$getAllResources$resources.get());

			return null;
		}

		return pack.open(type, id);
	}
}
