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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.pack.PackListWidget;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;

import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;

@Mixin(PackScreen.class)
public class PackScreenMixin {
	@Inject(method = "method_29672", at = @At("HEAD"), cancellable = true)
	private void addPackEntry(PackListWidget packListWidget, ResourcePackOrganizer.Pack pack, CallbackInfo info) {
		// Every mod resource packs should be hidden from the user.
		// Registered built-in resource packs should not be hidden as they are optional for the user.
		if (pack.getSource() == ModResourcePackCreator.RESOURCE_PACK_SOURCE) {
			info.cancel();
		}
	}
}
