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

package net.fabricmc.fabric.mixin.resource.loader.addpacks;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.SaveLoading;

import net.fabricmc.fabric.impl.resource.loader.addpacks.ResourcePackRegistrationEventsImpl;

@Mixin(SaveLoading.DataPacks.class)
public class SaveLoadingDataPacksMixin {
	@ModifyVariable(
			method = "load",
			at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/resource/ResourcePackManager;createResourcePacks()Ljava/util/List;"),
			index = 3
	)
	private List<ResourcePack> registerPacksAfterAll(List<ResourcePack> packs) {
		return ResourcePackRegistrationEventsImpl.addPacks(ResourceType.SERVER_DATA, packs);
	}
}
