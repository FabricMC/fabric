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

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.resource.ResourcePackSource;

import net.fabricmc.fabric.impl.resource.loader.FabricResource;

/**
 * Implements {@link FabricResource} for the anonymous resource implementation
 * in {@link net.minecraft.resource.DefaultResourcePack#getResource}.
 */
@Mixin(targets = "net/minecraft/resource/DefaultResourcePack$1")
abstract class DefaultResourcePackResourceMixin implements FabricResource {
	@Override
	public ResourcePackSource getFabricPackSource() {
		// The default resource pack only contains built-in vanilla resources.
		return ResourcePackSource.PACK_SOURCE_BUILTIN;
	}
}
