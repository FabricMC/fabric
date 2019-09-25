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

import net.fabricmc.fabric.api.event.resource.PackScannerRegistrationCallback;
import net.fabricmc.fabric.impl.resources.ModResourcePackCreator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.ClientResourcePackContainer;
import net.minecraft.resource.ResourcePackContainerManager;
import net.minecraft.resource.ResourcePackCreator;
import net.minecraft.resource.ResourceType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftClient.class)
public class MixinMinecraftGame {
	@Shadow
	@Final
	private ResourcePackContainerManager<ClientResourcePackContainer> resourcePackContainerManager;

	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourcePackContainerManager;addCreator(Lnet/minecraft/resource/ResourcePackCreator;)V", ordinal = 1))
	public void initResources(ResourcePackContainerManager<ClientResourcePackContainer> manager, ResourcePackCreator creator) {
		manager.addCreator(creator);
		manager.addCreator(new ModResourcePackCreator(ResourceType.CLIENT_RESOURCES));
		PackScannerRegistrationCallback.RESOURCE.invoker().registerTo(manager);
	}
}
