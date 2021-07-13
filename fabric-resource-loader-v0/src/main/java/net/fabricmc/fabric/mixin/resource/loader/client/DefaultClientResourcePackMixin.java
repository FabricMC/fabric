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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.resource.DefaultClientResourcePack;
import net.minecraft.client.resource.ResourceIndex;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.mixin.resource.loader.DefaultResourcePackMixin;

/**
 * Override the overwritten open() method to correctly load the client's extra assets.
 */
@Mixin(DefaultClientResourcePack.class)
public abstract class DefaultClientResourcePackMixin extends DefaultResourcePackMixin {
	@Final
	@Shadow
	private ResourceIndex index;

	@Override
	public InputStream open(ResourceType type, Identifier id) throws IOException {
		if (type == ResourceType.CLIENT_RESOURCES) {
			File file = this.index.getResource(id);

			if (file != null && file.exists()) {
				try {
					return new FileInputStream(file);
				} catch (FileNotFoundException ignored) {
					// Ignored.
				}
			}
		}

		return super.open(type, id);
	}
}
