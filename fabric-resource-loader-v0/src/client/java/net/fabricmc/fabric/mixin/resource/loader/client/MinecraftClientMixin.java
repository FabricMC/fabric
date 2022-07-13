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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackResourceMetadata;

import net.fabricmc.fabric.impl.resource.loader.FabricPackResourceMetadata;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Redirect(method = "createResourcePackProfile", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/metadata/PackResourceMetadata;getPackFormat()I"))
	private static int getFabricPackFormat(PackResourceMetadata instance) {
		return ((FabricPackResourceMetadata) instance).getPackFormat(ResourceType.CLIENT_RESOURCES);
	}
}
