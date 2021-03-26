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

package net.fabricmc.fabric.mixin.conditionalresource;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;

import net.fabricmc.fabric.impl.conditionalresource.NamespaceResourceManagerExtensions;
import net.fabricmc.fabric.impl.conditionalresource.WrappedResourcePack;

@Mixin(NamespaceResourceManager.class)
public abstract class NamespaceResourceManagerMixin implements NamespaceResourceManagerExtensions {
	@Shadow
	@Final
	protected List<ResourcePack> packList;
	@Shadow
	@Final
	private ResourceType type;
	@Shadow
	@Final
	private String namespace;

	@Override
	public void fabric_indexFabricMeta() {
		for (ResourcePack pack : this.packList) {
			if (pack instanceof WrappedResourcePack) {
				((WrappedResourcePack) pack).fabric_indexFabricMeta(this.type, this.namespace);
			}
		}
	}

	@ModifyVariable(method = "addPack", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private ResourcePack addPack(ResourcePack pack) {
		return new WrappedResourcePack(pack);
	}
}
