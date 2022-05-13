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

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;

import net.fabricmc.fabric.impl.resource.loader.FabricLifecycledResourceManager;

@Mixin(LifecycledResourceManagerImpl.class)
public class LifecycledResourceManagerImplMixin implements FabricLifecycledResourceManager {
	@Unique
	private ResourceType fabric_ResourceType;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void init(ResourceType resourceType, List<ResourcePack> list, CallbackInfo ci) {
		this.fabric_ResourceType = resourceType;
	}

	@Override
	public ResourceType fabric_getResourceType() {
		return fabric_ResourceType;
	}
}
