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

package net.fabricmc.fabric.mixin.resources;

import net.fabricmc.fabric.impl.resources.CustomImageResourcePackInfo;
import net.fabricmc.fabric.impl.resources.EnhancedResourcePackProfile;
import net.minecraft.client.resource.ClientResourcePackContainer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mixin(ResourcePackContainer.class)
public abstract class ResourcePackProfileMixin implements EnhancedResourcePackProfile {
	private List<Supplier<? extends ResourcePack>> lowerPacks = new ArrayList<>();
	private List<Supplier<? extends ResourcePack>> higherPacks = new ArrayList<>();

	@Override
	public List<Supplier<? extends ResourcePack>> getLowerPriorityPacks() {
		return lowerPacks;
	}

	@Override
	public List<Supplier<? extends ResourcePack>> getHigherPriorityPacks() {
		return higherPacks;
	}
}
