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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

@Mixin(DefaultResourcePack.class)
public class MixinDefaultResourcePack {
	@Inject(method = "findInputStream", at = @At("HEAD"), cancellable = true)
	protected void onFindInputStream(ResourceType resourceType, Identifier identifier, CallbackInfoReturnable<InputStream> callback) {
		if (DefaultResourcePack.resourcePath != null) {
			// Fall through to Vanilla logic, they have a special case here.
			return;
		}

		String path = resourceType.getDirectory() + "/" + identifier.getNamespace() + "/" + identifier.getPath();
		URL found = null;

		try {
			Enumeration<URL> candidates = DefaultResourcePack.class.getClassLoader().getResources(path);

			// Get the last element
			while (candidates.hasMoreElements()) {
				found = candidates.nextElement();
			}

			if (found == null || !DirectoryResourcePack.isValidPath(new File(found.getFile()), "/" + path)) {
				// Mimics vanilla behavior

				callback.setReturnValue(null);
				return;
			}
		} catch (IOException var6) {
			// Default path
		}

		try {
			if (found != null) {
				callback.setReturnValue(found.openStream());
			}
		} catch (Exception e) {
			// Default path
		}
	}
}
