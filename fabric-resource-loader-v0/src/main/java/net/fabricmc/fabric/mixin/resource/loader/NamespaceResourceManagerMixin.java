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

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.resource.loader.FabricResourceImpl;
import net.fabricmc.fabric.impl.resource.loader.GroupResourcePack;
import net.fabricmc.fabric.impl.resource.loader.ResourcePackSourceTracker;

@Mixin(NamespaceResourceManager.class)
public class NamespaceResourceManagerMixin {
	private final ThreadLocal<List<Resource>> fabric$getAllResources$resources = new ThreadLocal<>();

	@Inject(
			method = "getAllResources",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/resource/NamespaceResourceManager;getMetadataPath(Lnet/minecraft/util/Identifier;)Lnet/minecraft/util/Identifier;"
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void onGetAllResources(Identifier id, CallbackInfoReturnable<List<Resource>> cir, List<Resource> resources) {
		this.fabric$getAllResources$resources.set(resources);
	}

	@Redirect(
			method = "getAllResources",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/resource/ResourcePack;contains(Lnet/minecraft/resource/ResourceType;Lnet/minecraft/util/Identifier;)Z"
			)
	)
	private boolean onResourceAdd(ResourcePack pack, ResourceType type, Identifier id) throws IOException {
		if (pack instanceof GroupResourcePack) {
			((GroupResourcePack) pack).appendResources((NamespaceResourceManagerAccessor) this, id, this.fabric$getAllResources$resources.get());

			return false;
		}

		return pack.contains(type, id);
	}

	/* The two injectors below set the resource pack sources (see FabricResourceImpl)
	 * for resources created in NamespaceResourceManager.getAllResources and NamespaceResourceManager.getResource.
	 *
	 * Since (in 1.18.2) ResourceImpl doesn't hold a reference to its resource pack,
	 * we have to get the source from the resource pack when the resource is created.
	 * These are the main creation sites for resources in 1.18.2
	 * along with DefaultResourcePack.getResource and Fabric API's GroupResourcePack,
	 * which also either track the source similarly or provide other types of Resource instances
	 * that have a different FabricResource implementation.
	 */

	@Inject(method = "getAllResources", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", remap = false, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
	private void trackSourceOnGetAllResources(Identifier id, CallbackInfoReturnable<List<Resource>> cir, List<Resource> resources, Identifier metadataPath, Iterator<ResourcePack> packs, ResourcePack resourcePack) {
		// After the created resource has been added, read it from the list and set its source
		// to match the tracked source of its resource pack.
		if (resources.get(resources.size() - 1) instanceof FabricResourceImpl resource) {
			resource.setFabricPackSource(ResourcePackSourceTracker.getSource(resourcePack));
		}
	}

	@Inject(method = "getResource", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void trackSourceOnGetResource(Identifier id, CallbackInfoReturnable<Resource> cir, ResourcePack metaResourcePack, Identifier metadataPath, int i, ResourcePack resourcePack) {
		// Set the resource's source to match the tracked source of its resource pack.
		if (cir.getReturnValue() instanceof FabricResourceImpl resource) {
			resource.setFabricPackSource(ResourcePackSourceTracker.getSource(resourcePack));
		}
	}
}
