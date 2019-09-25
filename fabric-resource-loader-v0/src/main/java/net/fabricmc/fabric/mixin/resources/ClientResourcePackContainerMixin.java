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
import net.minecraft.client.resource.ClientResourcePackContainer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;
import java.io.InputStream;

@Mixin(ClientResourcePackContainer.class)
public abstract class ClientResourcePackContainerMixin implements CustomImageResourcePackInfo {
	@Shadow
	private NativeImage icon;

	@Override
	public void setImage(ResourcePack pack, String imagePath) {
		if (this.icon != null)
			return;

		try (InputStream inputStream_1 = pack.openRoot(imagePath)) {
			this.icon = NativeImage.read(inputStream_1);
		} catch (IllegalArgumentException | IOException ignored) {
		}
	}
}
